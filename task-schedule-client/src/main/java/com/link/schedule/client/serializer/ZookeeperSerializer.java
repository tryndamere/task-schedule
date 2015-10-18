package com.link.schedule.client.serializer;

/**
 * Created by rocky on 15/10/17.
 */
public interface ZookeeperSerializer<T> {

    /**
     * 序列化
     * @param t 序列化对象
     * @return
     */
    byte[] serializer(T t) throws SerializationException;

    /**
     * 反序列化
     * @param data
     * @return
     */
    T deserializer(byte[] data) throws SerializationException;

}
