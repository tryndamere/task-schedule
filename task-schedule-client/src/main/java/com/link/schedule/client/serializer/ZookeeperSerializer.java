package com.link.schedule.client.serializer;

/**
 * Created by rocky on 15/10/17.
 */
public interface ZookeeperSerializer {

    /**
     * 序列化
     * @param t 序列化对象
     * @return
     */
    <T> byte[] serializer(T t) throws SerializationException;

    /**
     * 反序列化
     * @param data
     * @return
     */
    <T> T deserializer(byte[] data , Class<T> tClass) throws SerializationException;

    byte[] serializer(String string) throws SerializationException;

    String deserializer(byte []bytes) throws SerializationException;

}
