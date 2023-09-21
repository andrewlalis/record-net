package com.andrewlalis.record_net;

public class UnknownMessageIdException extends RuntimeException {
    public final int messageId;

    public UnknownMessageIdException(int messageId) {
        super("Unknown record-net message id " + messageId);
        this.messageId = messageId;
    }
}
