package com.link.schedule.client.support;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Created by rocky on 15/10/17.
 */
public class TaskMethodRunnable implements Runnable {

    private Object target;

    private Method method;

    private String key;

    public TaskMethodRunnable(Object target, Method method , String key) {
        this.target = target;
        this.method = method;
        this.key    = key;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public String getKey() {
        return key;
    }

    public void run() {
        try {
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(this.target , this.key);
        } catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        } catch (InvocationTargetException ex) {
            ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
        }
    }

}
