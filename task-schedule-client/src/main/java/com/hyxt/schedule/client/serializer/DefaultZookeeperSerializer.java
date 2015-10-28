package com.hyxt.schedule.client.serializer;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by rocky on 2015/10/19.
 */
public class DefaultZookeeperSerializer implements ZookeeperSerializer {

    private Charset charset;

    public DefaultZookeeperSerializer() {
        this(Charset.forName("UTF-8"));
    }

    public DefaultZookeeperSerializer(Charset charset) {
        Assert.notNull(charset);
        this.charset = charset;
    }

    public <T> byte[] serializer(final T t) throws SerializationException {
        final StringBuilder stringBuilder = new StringBuilder(20);
        ReflectionUtils.doWithFields(t.getClass(), new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
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
        String encodeStr = stringBuilder.length() == 0 ? null : stringBuilder.deleteCharAt(stringBuilder.length()-1).toString();
        if (encodeStr == null) {
            return null;
        }
        return serializer(encodeStr);
    }

    public <T> T deserializer(byte[] data , Class<T> tClass) throws SerializationException {
        String deserializer = deserializer(data);
        if (deserializer != null) {
            try {
                T t = tClass.newInstance();
                for (String keyAndValue : deserializer.split("\\&")) {
                    String[] split = keyAndValue.split("\\=");
                    String key = split[0];
                    String value = split[1];
                    Field field = ReflectionUtils.findField(tClass, key);
                    ReflectionUtils.makeAccessible(field);
                    field.set(t , value);
                }
                return t;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public byte[] serializer(String string) throws SerializationException {
        try {
            return string == null || "".equals(string) ? null : URLEncoder.encode(string , this.charset.displayName()).getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("serialization fail" , e);
        }
    }

    public String deserializer(byte[] bytes) throws SerializationException {
        try {
            return bytes == null || bytes.length == 0 ? null : URLDecoder.decode(new String(bytes , this.charset.displayName()) , this.charset.displayName());
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("serialization fail" , e);
        }
    }

    private boolean isSerializer(Field field) {
        Class<?> type = field.getType();
        boolean isNeedSerializer = false;
        if ( type == String.class || type == Integer.class
                || type == Long.class || type == Short.class
                || type == Float.class || type == Double.class
                || type == Boolean.class) {
            isNeedSerializer = true;
        }
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
                && !Modifier.isFinal(modifiers) && isNeedSerializer;
    }

}
