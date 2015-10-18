package com.link.schedule.client.serializer;

import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by rocky on 15/10/17.
 */
public class StringZookeeperSerializer implements ZookeeperSerializer<String> {

    private Charset charset;

    public StringZookeeperSerializer() {
        this.charset = Charset.forName("UTF-8");
    }

    public StringZookeeperSerializer(Charset charset) {
        Assert.notNull(charset);
        this.charset = charset;
    }

    public byte[] serializer(String string) throws SerializationException {
        try {
            return (string == null ? null : URLEncoder.encode(string, charset.displayName()).getBytes(this.charset));
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("serialization fail" , e);
        }
    }

    public String deserializer(byte[] data) throws SerializationException {
        try {
            return data == null ? null : URLDecoder.decode(new String(data , this.charset) , charset.displayName());
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("deserializer fail" , e);
        }
    }

}
