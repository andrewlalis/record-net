package com.andrewlalis.record_net;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;

/**
 * A collection of the information that's needed about a record in order to
 * serialize and deserialize it at runtime.
 * @param components The record's ordered array of components.
 * @param constructor The canonical constructor for the record.
 * @param <T> The type of the record.
 */
record RecordInfo<T>(RecordComponent[] components, Constructor<T> constructor) {
    /**
     * Prepares an instance of RecordInfo for a given record class.
     * @param type The record class.
     * @return The RecordInfo object.
     * @param <T> The type of the record.
     */
    public static <T> RecordInfo<T> forType(Class<T> type) {
        if (!type.isRecord()) throw new IllegalArgumentException(type + " is not a record.");
        RecordComponent[] c = type.getRecordComponents();
        Class<?>[] paramTypes = new Class<?>[c.length];
        for (int i = 0; i < c.length; i++) {
            paramTypes[i] = c[i].getType();
        }
        try {
            Constructor<T> ctor = type.getDeclaredConstructor(paramTypes);
            return new RecordInfo<>(c, ctor);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
