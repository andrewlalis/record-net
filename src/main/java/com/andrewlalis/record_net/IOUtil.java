package com.andrewlalis.record_net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * A utility class that contains functions for IO operations on certain data
 * types. It cannot be instantiated; use its static methods only.
 */
public final class IOUtil {
    private IOUtil() {}

    /**
     * Determines if a type is a primitive, or a wrapper type for a primitive.
     * @param type The type to check.
     * @return True if the type is primitive or a wrapper.
     */
    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        final Set<Class<?>> types = Set.of(Byte.class, Short.class, Integer.class, Character.class, Float.class, Double.class, Long.class, Boolean.class);
        return type.isPrimitive() || types.contains(type);

    }

    /**
     * Reads a primitive type from an input stream.
     * @param type The type to read.
     * @param dIn The stream to read from.
     * @return The object representation of the primitive that was read.
     * @throws IOException If an error occurs.
     */
    public static Object readPrimitive(Class<?> type, DataInputStream dIn) throws IOException {
        if (type.equals(Integer.class) || type.equals(int.class)) return dIn.readInt();
        if (type.equals(Short.class) || type.equals(short.class)) return dIn.readShort();
        if (type.equals(Byte.class) || type.equals(byte.class)) return dIn.readByte();
        if (type.equals(Character.class) || type.equals(char.class)) return dIn.readChar();
        if (type.equals(Long.class) || type.equals(long.class)) return dIn.readLong();
        if (type.equals(Float.class) || type.equals(float.class)) return dIn.readFloat();
        if (type.equals(Double.class) || type.equals(double.class)) return dIn.readDouble();
        if (type.equals(Boolean.class) || type.equals(boolean.class)) return dIn.readBoolean();
        throw new IllegalArgumentException("Type " + type.getSimpleName() + " is not primitive.");
    }

    /**
     * Writes a primitive object to an output stream.
     * @param obj The primitive (wrapped in an object) to write.
     * @param dOut The stream to write to.
     * @throws IOException If an error occurs.
     */
    public static void writePrimitive(Object obj, DataOutputStream dOut) throws IOException {
        switch (obj) {
            case Integer n -> dOut.writeInt(n);
            case Short n -> dOut.writeShort(n);
            case Byte n -> dOut.writeByte(n);
            case Character c -> dOut.writeChar(c);
            case Long n -> dOut.writeLong(n);
            case Float n -> dOut.writeFloat(n);
            case Double n -> dOut.writeDouble(n);
            case Boolean b -> dOut.writeBoolean(b);
            default -> throw new IllegalArgumentException("Type " + obj.getClass().getSimpleName() + " is not primitive.");
        }
    }

    /**
     * Reads a string from an input stream.
     * @see java.io.DataInput#readUTF()
     * @param dIn The stream to read from.
     * @return The string that was read.
     * @throws IOException If an error occurs.
     */
    public static String readString(DataInputStream dIn) throws IOException {
        return dIn.readUTF();
    }

    /**
     * Writes a string to an output stream.
     * @see java.io.DataOutput#writeUTF(String)
     * @param s The string to write.
     * @param dOut The stream to write to.
     * @throws IOException If an error occurs.
     */
    public static void writeString(String s, DataOutputStream dOut) throws IOException {
        dOut.writeUTF(s);
    }

    /**
     * Reads a UUID as two longs being the most significant, and least
     * significant bits, respectively.
     * @param dIn The input stream to read from.
     * @return The UUID that was read.
     * @throws IOException If an error occurs.
     */
    public static UUID readUUID(DataInputStream dIn) throws IOException {
        long n1 = dIn.readLong();
        long n2 = dIn.readLong();
        return new UUID(n1, n2);
    }

    /**
     * Writes a UUID as two longs with the most significant bits first, and
     * then the least significant bits.
     * @param uuid The UUID to write.
     * @param dOut The output stream to write to.
     * @throws IOException If an error occurs.
     */
    public static void writeUUID(UUID uuid, DataOutputStream dOut) throws IOException {
        dOut.writeLong(uuid.getMostSignificantBits());
        dOut.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Reads an enum value from an input stream. Uses the enum value's ordinal
     * integer value to convert, or -1 in the case of null.
     * @param type The enum type to read.
     * @param dIn The stream to read from.
     * @return The enum type that was read.
     * @param <T> The type of the enum.
     * @throws IOException If an error occurs.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T readEnum(Class<?> type, DataInputStream dIn) throws IOException {
        if (!type.isEnum()) throw new IllegalArgumentException("Type must be an enum.");
        int ordinal = dIn.readInt();
        if (ordinal == -1) return null;
        return (T) type.getEnumConstants()[ordinal];
    }

    /**
     * Writes an enum value to an output stream. Uses the enum value's ordinal
     * integer value, or -1 in the case of null.
     * @param value The enum value to write.
     * @param dOut The stream to write to.
     * @throws IOException If an error occurs.
     */
    public static void writeEnum(Enum<?> value, DataOutputStream dOut) throws IOException {
        if (value == null) {
            dOut.writeInt(-1);
        } else {
            dOut.writeInt(value.ordinal());
        }
    }

    /**
     * Reads an array of primitive values of a given type from a stream.
     * <p>
     *     Note that because Java doesn't support primitive generics yet, we
     *     return an <code>Object</code> which can be cast to the primitive
     *     array type you're expecting.
     * </p>
     * @param type The primitive array type.
     * @param dIn The stream to read from.
     * @return The array that was read.
     * @throws IOException If an error occurs.
     * @throws IllegalArgumentException If the given type is not a primitive array.
     */
    public static Object readPrimitiveArray(Class<?> type, DataInputStream dIn) throws IOException {
        final var cType = type.getComponentType();
        if (cType.equals(byte.class)) return readByteArray(dIn);
        if (cType.equals(short.class)) return readShortArray(dIn);
        if (cType.equals(int.class)) return readIntArray(dIn);
        if (cType.equals(long.class)) return readLongArray(dIn);
        if (cType.equals(float.class)) return readFloatArray(dIn);
        if (cType.equals(double.class)) return readDoubleArray(dIn);
        if (cType.equals(boolean.class)) return readBooleanArray(dIn);
        if (cType.equals(char.class)) return readCharArray(dIn);
        throw new IllegalArgumentException("Type " + type + " is not a primitive array.");
    }

    /**
     * Writes an array of primitive values to a stream.
     * @param array The array to write.
     * @param dOut The stream to write to.
     * @throws IOException If an error occurs.
     * @throws IllegalArgumentException If the given type is not a primitive array.
     */
    public static void writePrimitiveArray(Object array, DataOutputStream dOut) throws IOException {
        switch (array) {
            case byte[] a -> writeByteArray(a, dOut);
            case short[] a -> writeShortArray(a, dOut);
            case int[] a -> writeIntArray(a, dOut);
            case long[] a -> writeLongArray(a, dOut);
            case float[] a -> writeFloatArray(a, dOut);
            case double[] a -> writeDoubleArray(a, dOut);
            case boolean[] a -> writeBooleanArray(a, dOut);
            case char[] a -> writeCharArray(a, dOut);
            default -> throw new IllegalArgumentException(array.getClass() + " is not a primitive array.");
        }
    }

    private static byte[] readByteArray(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        byte[] array = new byte[length];
        dIn.readFully(array);
        return array;
    }

    private static void writeByteArray(byte[] array, DataOutputStream dOut) throws IOException {
        dOut.writeInt(array.length);
        for (var element : array) dOut.writeByte(element);
    }

    private static short[] readShortArray(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        short[] array = new short[length];
        for (int i = 0; i < length; i++) {
            array[i] = dIn.readShort();
        }
        return array;
    }

    private static void writeShortArray(short[] array, DataOutputStream dOut) throws IOException {
        dOut.writeInt(array.length);
        for (var element : array) dOut.writeShort(element);
    }

    private static int[] readIntArray(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = dIn.readInt();
        }
        return array;
    }

    private static void writeIntArray(int[] array, DataOutputStream dOut) throws IOException {
        dOut.writeInt(array.length);
        for (var element : array) dOut.writeInt(element);
    }

    private static long[] readLongArray(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            array[i] = dIn.readLong();
        }
        return array;
    }

    private static void writeLongArray(long[] array, DataOutputStream dOut) throws IOException {
        dOut.writeInt(array.length);
        for (var element : array) dOut.writeLong(element);
    }

    private static float[] readFloatArray(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        float[] array = new float[length];
        for (int i = 0; i < length; i++) {
            array[i] = dIn.readFloat();
        }
        return array;
    }

    private static void writeFloatArray(float[] array, DataOutputStream dOut) throws IOException {
        dOut.writeInt(array.length);
        for (var element : array) dOut.writeFloat(element);
    }

    private static double[] readDoubleArray(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        double[] array = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = dIn.readDouble();
        }
        return array;
    }

    private static void writeDoubleArray(double[] array, DataOutputStream dOut) throws IOException {
        dOut.writeInt(array.length);
        for (var element : array) dOut.writeDouble(element);
    }

    private static boolean[] readBooleanArray(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        boolean[] array = new boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = dIn.readBoolean();
        }
        return array;
    }

    private static void writeBooleanArray(boolean[] array, DataOutputStream dOut) throws IOException {
        dOut.writeInt(array.length);
        for (var element : array) dOut.writeBoolean(element);
    }

    private static char[] readCharArray(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        char[] array = new char[length];
        for (int i = 0; i < length; i++) {
            array[i] = dIn.readChar();
        }
        return array;
    }

    private static void writeCharArray(char[] array, DataOutputStream dOut) throws IOException {
        dOut.writeInt(array.length);
        for (var element : array) dOut.writeChar(element);
    }
}
