package com.link.schedule.client.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.util.StringValueResolver;

/**
 * Created by rocky on 2015/10/16.
 */
public class TaskAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered,
        EmbeddedValueResolverAware, BeanFactoryAware, ApplicationContextAware,
        SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    private StringValueResolver resolver;

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

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    public void destroy() throws Exception {

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

    }
}
