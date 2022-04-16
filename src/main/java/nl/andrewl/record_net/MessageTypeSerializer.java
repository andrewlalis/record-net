package nl.andrewl.record_net;

import java.util.function.Function;

/**
 * A type serializer provides the basic components needed to read and write
 * instances of the given message type.
 * @param <T> The message type.
 */
public interface MessageTypeSerializer<T extends Message> {
    /**
     * Gets the class of the message type that this serializer handles.
     * @return The message class.
     */
    Class<T> messageClass();

    /**
     * Gets a function that computes the size, in bytes, of messages of this
     * serializer's type.
     * @return A byte size function.
     */
    Function<T, Integer> byteSizeFunction();

    /**
     * Gets a component that can read messages from an input stream.
     * @return The message reader.
     */
    MessageReader<T> reader();

    /**
     * Gets a component that can write messages to an output stream.
     * @return The message writer.
     */
    MessageWriter<T> writer();
}
