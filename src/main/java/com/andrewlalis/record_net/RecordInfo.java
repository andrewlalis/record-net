package com.andrewlalis.record_net;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;

record RecordInfo<T>(RecordComponent[] components, Constructor<T> constructor) {
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
