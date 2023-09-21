package com.andrewlalis.record_net;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class RecordMappedSerializerTest {
    @Test
    public void testRegisterType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RecordMappedSerializer().registerType(1, String.class);
        });
        assertDoesNotThrow(() -> {
            record TmpRecord (int a) {}
            new RecordMappedSerializer().registerType(1, TmpRecord.class);
        });
        RecordMappedSerializer serializer = new RecordMappedSerializer();
        record TmpRecord1 (int a) {}
        assertFalse(serializer.isTypeSupported(TmpRecord1.class));
        serializer.registerType(1, TmpRecord1.class);
        assertTrue(serializer.isTypeSupported(TmpRecord1.class));
    }

    @Test
    public void testBasicReadAndWrite() throws Exception {
        record TmpRecordA (int a, float b, byte[] c) {}
        RecordMappedSerializer serializer = new RecordMappedSerializer();
        serializer.registerType(1, TmpRecordA.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TmpRecordA testObj = new TmpRecordA(54, 234.543f, new byte[]{1, 2, 3});
        serializer.writeMessage(testObj, baos);
        byte[] data = baos.toByteArray();

        Object obj = serializer.readMessage(new ByteArrayInputStream(data));
        assertInstanceOf(TmpRecordA.class, obj);
        TmpRecordA objA = (TmpRecordA) obj;
        assertEquals(testObj.a, objA.a);
        assertEquals(testObj.b, objA.b);
        assertArrayEquals(testObj.c, objA.c);
    }

    @Test
    public void testNestedRecords() throws Exception {
        record RecordA (int a, int b, float x) {}
        record RecordB (RecordA a, boolean flag) {}
        RecordMappedSerializer serializer = new RecordMappedSerializer();
        serializer.registerType(1, RecordA.class);
        serializer.registerType(2, RecordB.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RecordB testObj = new RecordB(new RecordA(1, 2, 3.5f), false);
        serializer.writeMessage(testObj, baos);
        byte[] data = baos.toByteArray();

        Object obj = serializer.readMessage(new ByteArrayInputStream(data));
        assertInstanceOf(RecordB.class, obj);
        RecordB b = (RecordB) obj;
        assertEquals(testObj, b);
    }
}
