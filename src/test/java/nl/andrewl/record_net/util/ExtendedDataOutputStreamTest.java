package nl.andrewl.record_net.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtendedDataOutputStreamTest {

    @Test
    public void testWriteString() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ExtendedDataOutputStream eOut = new ExtendedDataOutputStream(bOut);
        eOut.writeString("Hello world!");
        byte[] data = bOut.toByteArray();
        assertEquals(4 + "Hello world!".length(), data.length);
        DataInputStream dIn = new DataInputStream(new ByteArrayInputStream(data));
        assertEquals(12, dIn.readInt());
        String s = new String(dIn.readNBytes(12));
        assertEquals("Hello world!", s);

        bOut.reset();
        eOut.writeString(null);
        data = bOut.toByteArray();
        assertEquals(4, data.length);
        dIn = new DataInputStream(new ByteArrayInputStream(data));
        assertEquals(-1, dIn.readInt());
    }
}
