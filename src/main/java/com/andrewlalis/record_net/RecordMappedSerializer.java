package com.andrewlalis.record_net;

import java.io.*;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecordMappedSerializer implements RecordSerializer {
    private final Map<Integer, Class<?>> messageTypes = new HashMap<>();
    private final Map<Class<?>, Integer> messageTypeIds = new HashMap<>();
    private final Map<Class<?>, RecordInfo<?>> messageRecordInfo = new HashMap<>();

    public void registerType(int id, Class<?> type) {
        if (!type.isRecord()) throw new IllegalArgumentException("Only records are permitted.");
        this.messageTypes.put(id, type);
        this.messageTypeIds.put(type, id);
        this.messageRecordInfo.put(type, RecordInfo.forType(type));
    }

    public boolean isTypeSupported(Class<?> type) {
        return messageTypeIds.containsKey(type);
    }

    @Override
    public Object readMessage(InputStream in) throws IOException {
        var dIn = new DataInputStream(in);
        int id = dIn.readInt();
        Class<?> msgType = messageTypes.get(id);
        if (msgType == null) throw new UnknownMessageIdException(id);
        return readRawObject(dIn, msgType);
    }

    private Object readRawObject(DataInputStream dIn, Class<?> type) throws IOException {
        if (messageRecordInfo.containsKey(type)) {
            RecordInfo<?> recordInfo = messageRecordInfo.get(type);
            Object[] values = new Object[recordInfo.components().length];
            for (int i = 0; i < recordInfo.components().length; i++) {
                values[i] = readRawObject(dIn, recordInfo.components()[i].getType());
            }
            try {
                return recordInfo.constructor().newInstance(values);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        if (type.isArray()) {
            if (type.getComponentType().isPrimitive()) {
                return IOUtil.readPrimitiveArray(type, dIn);
            } else {
                int length = dIn.readInt();
                Object[] array = new Object[length];
                for (int i = 0; i < length; i++) {
                    array[i] = readRawObject(dIn, type.getComponentType());
                }
                return array;
            }
        }
        if (type.isEnum()) {
            return IOUtil.readEnum(type, dIn);
        }
        if (type.equals(UUID.class)) {
            return IOUtil.readUUID(dIn);
        }
        return IOUtil.readPrimitive(type, dIn);
    }

    @Override
    public void writeMessage(Object msg, OutputStream out) throws IOException {
        if (msg == null) throw new IllegalArgumentException("Cannot write a null message.");
        if (!isTypeSupported(msg.getClass())) throw new UnsupportedMessageTypeException(msg.getClass());
        var dOut = new DataOutputStream(out);
        int id = messageTypeIds.get(msg.getClass());
        dOut.writeInt(id);
        writeRawObject(msg, dOut);
    }

    private void writeRawObject(Object obj, DataOutputStream dOut) throws IOException {
        final Class<?> type = obj.getClass();
        if (messageRecordInfo.containsKey(type)) {
            RecordInfo<?> recordInfo = messageRecordInfo.get(type);
            for (var component : recordInfo.components()) {
                try {
                    writeRawObject(component.getAccessor().invoke(obj), dOut);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (type.isArray()) {
            if (type.getComponentType().isPrimitive()) {
                IOUtil.writePrimitiveArray(obj, dOut);
            } else {
                int length = Array.getLength(obj);
                dOut.writeInt(length);
                for (int i = 0; i < length; i++) {
                    writeRawObject(Array.get(obj, i), dOut);
                }
            }
        } else if (type.isEnum()) {
            IOUtil.writeEnum((Enum<?>) obj, dOut);
        } else if (type.equals(UUID.class)) {
            IOUtil.writeUUID((UUID) obj, dOut);
        } else {
            IOUtil.writePrimitive(obj, dOut);
        }
    }
}
