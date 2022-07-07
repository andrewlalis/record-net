package nl.andrewl.record_net.util;

import nl.andrewl.record_net.Message;
import nl.andrewl.record_net.MessageTypeSerializer;
import nl.andrewl.record_net.Serializer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * An extended output stream which contains additional methods for reading more
 * complex types that are used by the Concord system.
 */
public class ExtendedDataInputStream extends DataInputStream {
	private final Serializer serializer;

	public ExtendedDataInputStream(Serializer serializer, InputStream in) {
		super(in);
		this.serializer = serializer;
	}

	public ExtendedDataInputStream(Serializer serializer, byte[] data) {
		this(serializer, new ByteArrayInputStream(data));
	}

	public String readString() throws IOException {
		int length = super.readInt();
		if (length == -1) return null;
		if (length == 0) return "";
		byte[] data = new byte[length];
		int read = super.read(data);
		if (read != length) throw new IOException("Not all bytes of a string of length " + length + " could be read.");
		return new String(data, StandardCharsets.UTF_8);
	}

	public <T extends Enum<?>> T readEnum(Class<T> e) throws IOException {
		int ordinal = super.readInt();
		if (ordinal == -1) return null;
		return e.getEnumConstants()[ordinal];
	}

	public UUID readUUID() throws IOException {
		long a = super.readLong();
		long b = super.readLong();
		if (a == -1 && b == -1) {
			return null;
		}
		return new UUID(a, b);
	}

	public byte[] readByteArray() throws IOException {
		int length = readInt();
		if (length < 0) return null;
		byte[] array = new byte[length];
		int readLength = read(array);
		if (readLength != length) throw new IOException("Could not read complete byte array.");
		return array;
	}

	public int[] readIntArray() throws IOException {
		int length = readInt();
		if (length < 0) return null;
		int[] array = new int[length];
		for (int i = 0; i < length; i++) {
			array[i] = readInt();
		}
		return array;
	}

	public float[] readFloatArray() throws IOException {
		int length = readInt();
		if (length < 0) return null;
		float[] array = new float[length];
		for (int i = 0; i < length; i++) {
			array[i] = readFloat();
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> T[] readArray(MessageTypeSerializer<T> type) throws IOException {
		int length = super.readInt();
		T[] array = (T[]) Array.newInstance(type.messageClass(), length);
		for (int i = 0; i < length; i++) {
			array[i] = type.reader().read(this);
		}
		return array;
	}

	/**
	 * Reads an object from the stream that is of a certain expected type.
	 * @param type The type of object to read.
	 * @return The object that was read.
	 * @throws IOException If an error occurs while reading.
	 */
	@SuppressWarnings("unchecked")
	public Object readObject(Class<?> type) throws IOException {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return readInt();
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			return readShort();
		} else if (type.equals(Byte.class) || type.equals(byte.class)) {
			return (byte) read();
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return readLong();
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			return readFloat();
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			return readDouble();
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			return readBoolean();
		} else if (type.equals(String.class)) {
			return readString();
		} else if (type.equals(UUID.class)) {
			return readUUID();
		} else if (type.isEnum()) {
			return readEnum((Class<? extends Enum<?>>) type);
		} else if (type.isAssignableFrom(byte[].class)) {
			return readByteArray();
		} else if (type.isAssignableFrom(int[].class)) {
			return readIntArray();
		} else if (type.isAssignableFrom(float[].class)) {
			return readFloatArray();
		} else if (type.isArray() && Message.class.isAssignableFrom(type.getComponentType())) {
			var messageType = RecordMessageTypeSerializer.get(serializer, (Class<? extends Message>) type.getComponentType());
			return readArray(messageType);
		} else if (Message.class.isAssignableFrom(type)) {
			var messageType = RecordMessageTypeSerializer.get(serializer, (Class<? extends Message>) type);
			return messageType.reader().read(this);
		} else {
			throw new IOException("Unsupported object type: " + type.getSimpleName());
		}
	}
}
