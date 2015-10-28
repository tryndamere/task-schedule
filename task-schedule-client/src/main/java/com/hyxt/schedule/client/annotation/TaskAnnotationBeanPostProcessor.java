package com.hyxt.schedule.client.annotation;

import com.hyxt.schedule.client.config.TaskRegistrar;
import com.hyxt.schedule.client.serializer.ZookeeperSerializer;
import com.hyxt.schedule.client.support.TaskMethodRunnable;
import com.hyxt.schedule.client.config.CronExpressTask;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rocky on 2015/10/16.
 */
public class TaskAnnotationBeanPostProcessor implements EnvironmentAware , BeanPostProcessor, Ordered,
        EmbeddedValueResolverAware, BeanFactoryAware, ApplicationContextAware,
        SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    private StringValueResolver resolver;

    private final Set<Class<?>> nonAnnotatedClasses =
            Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));

    private TaskRegistrar taskRegistrar = new TaskRegistrar();

    private CuratorFramework curatorFramework;

    private Environment environment;

    private ZookeeperSerializer zookeeperSerializer;

    public void setZookeeperSerializer(ZookeeperSerializer zookeeperSerializer) {
        this.zookeeperSerializer = zookeeperSerializer;
    }

    public void setCuratorFramework(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        if (beanFactory == null) {
            this.beanFactory = applicationContext;
        }
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            finishRegistration();
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            final Set<Method> annotatedMethods = new LinkedHashSet<Method>(1);
            ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    for (Annotation annotation : AnnotationUtils.getAnnotations(method)) {
                        if (annotation instanceof TaskDefinitionAndHandler) {
                            TaskDefinitionAndHandler taskDefinitionAndHandler = (TaskDefinitionAndHandler)annotation;
                            processTask(taskDefinitionAndHandler , method , bean);
                            annotatedMethods.add(method);
                        }
                    }
                }
            });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No @TaskDefinitionAndHandler annotations found on bean class: " + bean.getClass());
                }
            }
        }
        return bean;
    }

    private void processTask(TaskDefinitionAndHandler taskDefinitionAndHandler , Method method , Object bean) {
        Assert.isTrue(void.class.equals(method.getReturnType()) ,
                "Only void-returning methods may be annotated with @TaskDefinitionAndHandler");
        Assert.isTrue(method.getParameterTypes().length == 0 ,
                "Only no-arg methods may be annotated with @TaskDefinitionAndHandler");

        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                method = bean.getClass().getMethod(method.getName() , method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else if (AopUtils.isCglibProxy(bean)) {
            if (Modifier.isPrivate(method.getModifiers())) {
                throw new IllegalStateException(String.format(
                        "@TaskDefinitionAndHandler method '%s' found on CGLIB proxy for target class '%s' but cannot " +
                                "be delegated to target bean. Switch its visibility to package or protected.",
                        method.getName(), method.getDeclaringClass().getSimpleName()));
            }
        }

        CronExpressTask cronExpressTask = initCronExpressTask(taskDefinitionAndHandler, method);
        this.taskRegistrar.addCronExpressTask(cronExpressTask);

        Runnable runnable = new TaskMethodRunnable(bean, method);
        this.taskRegistrar.addTaskRunnable(runnable , cronExpressTask.getKey());

    }

    private CronExpressTask initCronExpressTask(TaskDefinitionAndHandler taskDefinitionAndHandler , Method method) {
        if (StringUtils.isEmpty(taskDefinitionAndHandler.key()) ||
                StringUtils.isEmpty(taskDefinitionAndHandler.cronExpress())) {
            throw new IllegalStateException(String.format("cronExpress or key is null , method : %s" , method.getName()));
        }
        return new CronExpressTask(taskDefinitionAndHandler.cronExpress() ,
                taskDefinitionAndHandler.desc() , taskDefinitionAndHandler.key() , taskDefinitionAndHandler.isConcurrent());

    }

    public void destroy() throws Exception {
        this.taskRegistrar.destroy();
    }

    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    public void afterSingletonsInstantiated() {
        if (this.applicationContext == null) {
            // Not running in an ApplicationContext -> register tasks early...
            finishRegistration();
        }
    }

    private void finishRegistration() {
        if (this.curatorFramework != null) {
            this.taskRegistrar.setCuratorFramework(this.curatorFramework);
        }

        if (this.environment != null) {
            this.taskRegistrar.setApplication(environment.getProperty("schedule.project.application"));
            this.taskRegistrar.setOwner(environment.getProperty("schedule.project.owner"));
        }

        if (this.zookeeperSerializer != null) {
            this.taskRegistrar.setZookeeperSerializer(this.zookeeperSerializer);
        }

        if (this.taskRegistrar.getCuratorFramework() == null) {
            Assert.state(this.beanFactory != null , "BeanFactory must be set to find task scheduler by type");
            try {
                this.taskRegistrar.setCuratorFramework(this.beanFactory.getBean(CuratorFramework.class));
            } catch (NoUniqueBeanDefinitionException ex) {
                throw new IllegalStateException("More than one CuratorFramework exists within the context.");
            }
        }

        this.taskRegistrar.afterPropertiesSet();
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
