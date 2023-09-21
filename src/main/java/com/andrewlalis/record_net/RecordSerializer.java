package com.andrewlalis.record_net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface RecordSerializer {
    Object readMessage(InputStream in) throws IOException;
    void writeMessage(Object msg, OutputStream out) throws IOException;
}
