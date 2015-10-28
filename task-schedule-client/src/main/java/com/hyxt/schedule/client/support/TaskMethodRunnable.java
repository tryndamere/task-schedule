package com.hyxt.schedule.client.support;

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

    public TaskMethodRunnable(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public void run() {
        try {
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(this.target);
        } catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        } catch (InvocationTargetException ex) {
            ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
        }
    }

}
