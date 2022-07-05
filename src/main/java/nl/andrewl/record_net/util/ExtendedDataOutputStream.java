package nl.andrewl.record_net.util;

import nl.andrewl.record_net.Message;
import nl.andrewl.record_net.Serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * An extended version of {@link DataOutputStream} with some extra methods
 * that help us to write more data.
 */
public class ExtendedDataOutputStream extends DataOutputStream {
	private final Serializer serializer;

	public ExtendedDataOutputStream(Serializer serializer, OutputStream out) {
		super(out);
		this.serializer = serializer;
	}

	/**
	 * Writes a string in length-prefixed form, where the 4-byte length of the
	 * string is written, followed by exactly that many bytes. If the string
	 * is null, then a length of -1 is written, and no bytes following it.
	 * @param s The string to write.
	 * @throws IOException If an error occurs while writing.
	 */
	public void writeString(String s) throws IOException {
		if (s == null) {
			writeInt(-1);
		} else {
			writeInt(s.length());
			write(s.getBytes(StandardCharsets.UTF_8));
		}
	}

	public void writeStrings(String... strings) throws IOException {
		for (var s : strings) {
			writeString(s);
		}
	}

	/**
	 * Writes an enum value as a 4-byte integer using the enum's ordinal
	 * position, or -1 if the given value is null.
	 * @param value The value to write.
	 * @throws IOException If an error occurs while writing.
	 */
	public void writeEnum(Enum<?> value) throws IOException {
		if (value == null) {
			writeInt(-1);
		} else {
			writeInt(value.ordinal());
		}
	}

	/**
	 * Writes a UUID as a 16-byte value. If the given UUID is null, then -1
	 * is written twice as two long (8 byte) values.
	 * @param uuid The value to write.
	 * @throws IOException If an error occurs while writing.
	 */
	public void writeUUID(UUID uuid) throws IOException {
		if (uuid == null) {
			writeLong(-1);
			writeLong(-1);
		} else {
			writeLong(uuid.getMostSignificantBits());
			writeLong(uuid.getLeastSignificantBits());
		}
	}

	/**
	 * Writes an array of messages using length-prefixed form. That is, we
	 * first write a 4-byte integer length that specifies how many items are in
	 * the array, followed by writing each element of the array. If the array
	 * is null, a length of -1 is written.
	 * @param array The array to write.
	 * @param <T> The type of items in the array.
	 * @throws IOException If an error occurs while writing.
	 */
	public <T extends Message> void writeArray(T[] array) throws IOException {
		if (array == null) {
			writeInt(-1);
		} else {
			writeInt(array.length);
			for (var item : array) writeMessage(item);
		}
	}

	public void writeArray(byte[] array) throws IOException {
		if (array == null) {
			writeInt(-1);
		} else {
			writeInt(array.length);
			write(array);
		}
	}

	public void writeArray(int[] array) throws IOException {
		if (array == null) {
			writeInt(-1);
		} else {
			writeInt(array.length);
			for (var item : array) writeInt(item);
		}
	}

	public void writeArray(float[] array) throws IOException {
		if (array == null) {
			writeInt(-1);
		} else {
			writeInt(array.length);
			for (var item : array) writeFloat(item);
		}
	}

	/**
	 * Writes a message using null-prefixed form. That is, we first write a
	 * boolean value which is false only if the message is null. Then, if the
	 * message is not null, we write it to the stream.
	 * @param msg The message to write.
	 * @param <T> The type of the message.
	 * @throws IOException If an error occurs while writing.
	 */
	public <T extends Message> void writeMessage(Message msg) throws IOException {
		writeBoolean(msg != null);
		if (msg != null) {
			msg.getTypeSerializer(serializer).writer().write(msg, this);
		}
	}

	/**
	 * Writes an object to the stream.
	 * @param o The object to write.
	 * @param type The object's type. This is needed in case the object itself
	 *             is null, which may be the case for some strings or ids.
	 * @throws IOException If an error occurs while writing, or if an
	 * unsupported object is supplied.
	 */
	public void writeObject(Object o, Class<?> type) throws IOException {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			writeInt((int) o);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			writeShort((short) o);
		} else if (type.equals(Byte.class) || type.equals(byte.class)) {
			writeByte((byte) o);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			writeLong((long) o);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			writeFloat((float) o);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			writeDouble((double) o);
		} else if (type.equals(String.class)) {
			writeString((String) o);
		} else if (type.equals(UUID.class)) {
			writeUUID((UUID) o);
		} else if (type.isEnum()) {
			writeEnum((Enum<?>) o);
		} else if (type.equals(byte[].class)) {
			writeArray((byte[]) o);
		} else if (type.isArray() && Message.class.isAssignableFrom(type.getComponentType())) {
			writeArray((Message[]) o);
		} else if (Message.class.isAssignableFrom(type)) {
			writeMessage((Message) o);
		} else {
			throw new IOException("Unsupported object type: " + o.getClass().getSimpleName());
		}
	}
}
