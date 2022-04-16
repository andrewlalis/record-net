package nl.andrewl.record_net;

import java.util.function.Function;

/**
 * Record containing the components needed to read and write a given message.
 * @param <T> The type of message.
 * @param messageClass The class of the message.
 * @param byteSizeFunction A function that computes the byte size of the message.
 * @param reader A reader that can read messages from an input stream.
 * @param writer A writer that write messages from an input stream.
 */
public record MessageTypeSerializerImpl<T extends Message>(
		Class<T> messageClass,
		Function<T, Integer> byteSizeFunction,
		MessageReader<T> reader,
		MessageWriter<T> writer
) implements MessageTypeSerializer<T> {}
