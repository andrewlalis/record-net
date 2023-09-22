package com.andrewlalis.record_net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines the basic interface for serialization and deserialization of
 * messages to and from streams.
 * @see RecordMappedSerializer
 */
public interface RecordSerializer {
    /**
     * Reads a message from an input stream.
     * @param in The input stream to read from.
     * @return The object that was read.
     * @throws IOException If an error occurs.
     */
    Object readMessage(InputStream in) throws IOException;

    /**
     * Writes a message to an output stream.
     * @param msg The message to write.
     * @param out The output stream to write to.
     * @throws IOException If an error occurs.
     */
    void writeMessage(Object msg, OutputStream out) throws IOException;
}
