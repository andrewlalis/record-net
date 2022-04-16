package nl.andrewl.record_net;

import nl.andrewl.record_net.util.ExtendedDataInputStream;
import nl.andrewl.record_net.util.ExtendedDataOutputStream;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for reading and writing messages from streams. It
 * also defines the set of supported message types, and their associated byte
 * identifiers, via the {@link Serializer#registerType(int, Class)} method.
 */
public class Serializer {
	/**
	 * The mapping which defines each supported message type and the byte value
	 * used to identify it when reading and writing messages.
	 */
	private final Map<Byte, MessageTypeSerializer<?>> messageTypes = new HashMap<>();

	/**
	 * An inverse of {@link Serializer#messageTypes} which is used to look up a
	 * message's byte value when you know the class of the message.
	 */
	private final Map<MessageTypeSerializer<?>, Byte> inverseMessageTypes = new HashMap<>();

	/**
	 * Constructs a new serializer instance.
	 */
	public Serializer() {}

	/**
	 * Constructs a serializer using a predefined mapping of message types and
	 * their ids.
	 * @param messageTypes A map containing message types mapped to their ids.
	 */
	public Serializer(Map<Byte, Class<? extends Message>> messageTypes) {
		messageTypes.forEach(this::registerType);
	}

	/**
	 * Helper method which registers a message type to be supported by the
	 * serializer, by adding it to the normal and inverse mappings.
	 * @param id The byte which will be used to identify messages of the given
	 *           class. The value should from 0 to 127.
	 * @param messageClass The type of message associated with the given id.
	 */
	public synchronized <T extends Message> void registerType(int id, Class<T> messageClass) {
		if (id < 0 || id > 127) throw new IllegalArgumentException("Invalid id.");
		MessageTypeSerializer<T> type = MessageTypeSerializer.get(messageClass);
		messageTypes.put((byte)id, type);
		inverseMessageTypes.put(type, (byte)id);
	}

	/**
	 * Reads a message from the given input stream and returns it, or throws an
	 * exception if an error occurred while reading from the stream.
	 * @param i The input stream to read from.
	 * @return The message which was read.
	 * @throws IOException If an error occurs while reading, such as trying to
	 * read an unsupported message type, or if a message object could not be
	 * constructed for the incoming data.
	 */
	public Message readMessage(InputStream i) throws IOException {
		ExtendedDataInputStream d = new ExtendedDataInputStream(i);
		byte typeId = d.readByte();
		var type = messageTypes.get(typeId);
		if (type == null) {
			throw new IOException("Unsupported message type: " + typeId);
		}
		try {
			return type.reader().read(d);
		} catch (IOException e) {
			throw new IOException("Could not instantiate new message object of type " + type.getClass().getSimpleName(), e);
		}
	}

	/**
	 * Reads a message from the given byte array and returns it, or throws an
	 * exception if an error occurred while reading from the stream.
	 * @param data The data to read from.
	 * @return The message which was read.
	 * @throws IOException If an error occurs while reading, such as trying to
	 * read an unsupported message type, or if a message object could not be
	 * constructed for the incoming data.
	 */
	public Message readMessage(byte[] data) throws IOException {
		return readMessage(new ByteArrayInputStream(data));
	}

	/**
	 * Writes a message to the given output stream.
	 * @param msg The message to write.
	 * @param o The output stream to write to.
	 * @param <T> The message type.
	 * @throws IOException If an error occurs while writing, or if the message
	 * to write is not supported by this serializer.
	 */
	public <T extends Message> void writeMessage(T msg, OutputStream o) throws IOException {
		DataOutputStream d = new DataOutputStream(o);
		Byte typeId = inverseMessageTypes.get(msg.getTypeSerializer());
		if (typeId == null) {
			throw new IOException("Unsupported message type: " + msg.getClass().getSimpleName());
		}
		d.writeByte(typeId);
		msg.getTypeSerializer().writer().write(msg, new ExtendedDataOutputStream(d));
		d.flush();
	}

	/**
	 * Writes a message as a byte array.
	 * @param msg The message to write.
	 * @return The bytes that were written.
	 * @param <T> The message type.
	 * @throws IOException If an error occurs while writing, or if the message
	 * to write is not supported by this serializer.
	 */
	public <T extends Message> byte[] writeMessage(T msg) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1 + msg.byteSize());
		writeMessage(msg, out);
		return out.toByteArray();
	}
}
