package com.link.schedule.client.serializer;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by rocky on 15/10/17.
 */
public class SerializationException extends NestedRuntimeException {

    /**
     * Constructs a new <code>SerializationException</code> instance.
     *
     * @param msg
     * @param cause
     */
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new <code>SerializationException</code> instance.
     *
     * @param msg
     */
    public SerializationException(String msg) {
        super(msg);
    }
}
