package com.andrewlalis.record_net;

/**
 * An exception that's thrown when attempting to serialize or deserialize an
 * unsupported type. Unsupported types are any complex types not registered
 * with a serializer, or unsupported by {@link IOUtil}.
 */
public class UnsupportedMessageTypeException extends RuntimeException {
    /**
     * The type that's not supported.
     */
    public Class<?> messageType;

    /**
     * Constructs a new exception with the given type.
     * @param messageType The unsupported type.
     */
    public UnsupportedMessageTypeException(Class<?> messageType) {
        super("The message type " + messageType.getSimpleName() + " is not supported.");
        this.messageType = messageType;
    }
}
