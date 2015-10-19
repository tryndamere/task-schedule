package com.link.schedule.client.serializer;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by rocky on 2015/10/19.
 */
public class DefaultZookeeperSerializer<T> implements ZookeeperSerializer<T> {

    private Charset charset;

    public DefaultZookeeperSerializer() {
        this(Charset.forName("UTF-8"));
    }

    public DefaultZookeeperSerializer(Charset charset) {
        Assert.notNull(charset);
        this.charset = charset;
    }

    public byte[] serializer(final T t) throws SerializationException {
        final StringBuilder stringBuilder = new StringBuilder(20);
        ReflectionUtils.doWithFields(t.getClass(), new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Object fieldValue = field.get(t);
                if (fieldValue != null) {
                    stringBuilder.append(field.getName()).append("=").append(fieldValue).append("&");
                }
            }
        }, new ReflectionUtils.FieldFilter() {
            public boolean matches(Field field) {
                if (isSerializer(field)) {
                    return true;
                }
                return false;
            }
        });
        try {
            return stringBuilder.length() == 0 ? null : URLEncoder.encode(stringBuilder.deleteCharAt(stringBuilder.length()-1).toString() , this.charset.displayName()).getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("serialization fail" , e);
        }
    }

    public T deserializer(byte[] data) throws SerializationException {
        return null;
    }

    private boolean isSerializer(Field field) {
        Class<?> type = field.getType();
        boolean isNeedSerializer = type == String.class && type == Integer.class
                && type == Long.class && type == Short.class
                && type == Float.class && type == Double.class
                && type == Boolean.class ;
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
                && !Modifier.isFinal(modifiers) && isNeedSerializer;
    }

}
