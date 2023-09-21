package com.andrewlalis.record_net;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IOUtilTest {
    @Test
    public void testReadPrimitive() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dOut = new DataOutputStream(baos);
        dOut.writeInt(42);
        dOut.writeShort(25565);
        dOut.writeByte(35);
        dOut.writeChar(234);
        dOut.writeLong(234843209243L);
        dOut.writeFloat(3.14f);
        dOut.writeDouble(2.17);
        dOut.writeBoolean(true);
        dOut.writeBoolean(false);
        byte[] data = baos.toByteArray();

        DataInputStream dIn = new DataInputStream(new ByteArrayInputStream(data));
        assertEquals(42, IOUtil.readPrimitive(Integer.class, dIn));
        assertEquals((short) 25565, IOUtil.readPrimitive(Short.class, dIn));
        assertEquals((byte) 35, IOUtil.readPrimitive(Byte.class, dIn));
        assertEquals((char) 234, IOUtil.readPrimitive(Character.class, dIn));
        assertEquals(234843209243L, IOUtil.readPrimitive(Long.class, dIn));
        assertEquals(3.14f, IOUtil.readPrimitive(Float.class, dIn));
        assertEquals(2.17, IOUtil.readPrimitive(Double.class, dIn));
        assertEquals(true, IOUtil.readPrimitive(Boolean.class, dIn));
        assertEquals(false, IOUtil.readPrimitive(Boolean.class, dIn));
    }
}
