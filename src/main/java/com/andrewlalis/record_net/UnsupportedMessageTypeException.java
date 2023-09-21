package com.andrewlalis.record_net;

public class UnsupportedMessageTypeException extends RuntimeException {
    public Class<?> messageType;
    public int messageId;

    public UnsupportedMessageTypeException(Class<?> messageType) {
        super("The message type " + messageType.getSimpleName() + " is not supported.");
        this.messageType = messageType;
    }

    public UnsupportedMessageTypeException(int messageId) {
        super("The message with id " + messageId + " is not supported.");
        this.messageId = messageId;
    }
}
