package com.andrewlalis.record_net;

/**
 * An exception that's thrown when a {@link RecordSerializer} reads a message
 * whose id is unknown (not previously registered with the serializer).
 */
public class UnknownMessageIdException extends RuntimeException {
    /**
     * The id of the message as it was received.
     */
    public final int messageId;

    /**
     * Constructs the exception with a given id.
     * @param messageId The id of the message.
     */
    public UnknownMessageIdException(int messageId) {
        super("Unknown record-net message id " + messageId);
        this.messageId = messageId;
    }
}
