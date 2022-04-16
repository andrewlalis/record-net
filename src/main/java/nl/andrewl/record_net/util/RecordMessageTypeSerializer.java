package nl.andrewl.record_net.util;

import nl.andrewl.record_net.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Helper class that contains logic for generating {@link MessageTypeSerializerImpl}
 * implementations for record classes.
 */
public class RecordMessageTypeSerializer {
    /**
     * An internal cache for storing generated type serializers.
     */
    private static final Map<Pair<Class<?>, Serializer>, MessageTypeSerializer<?>> generatedMessageTypes = new HashMap<>();

    /**
     * Gets the {@link MessageTypeSerializer} instance for a given message class, and
     * generates a new implementation if none exists yet.
     * @param serializer The serializer context to get a type serializer for.
     * @param messageClass The class of the message to get a type for.
     * @param <T> The type of the message.
     * @return The message type.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Message> MessageTypeSerializer<T> get(Serializer serializer, Class<T> messageClass) {
        return (MessageTypeSerializer<T>) generatedMessageTypes.computeIfAbsent(
                new Pair<>(messageClass, serializer),
                p -> generateForRecord(serializer, (Class<T>) p.first())
        );
    }

    /**
     * Generates a message type instance for a given class, using reflection to
     * introspect the fields of the message.
     * <p>
     *     Note that this only works for record-based messages.
     * </p>
     * @param serializer The serializer context to get a type serializer for.
     * @param messageTypeClass The class of the message type.
     * @param <T> The type of the message.
     * @return A message type instance.
     */
    public static <T extends Message> MessageTypeSerializerImpl<T> generateForRecord(Serializer serializer, Class<T> messageTypeClass) {
        RecordComponent[] components = messageTypeClass.getRecordComponents();
        if (components == null) throw new IllegalArgumentException("Cannot generate a MessageTypeSerializer for non-record class " + messageTypeClass.getSimpleName());
        Constructor<T> constructor;
        try {
            constructor = messageTypeClass.getDeclaredConstructor(Arrays.stream(components)
                    .map(RecordComponent::getType).toArray(Class<?>[]::new));
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        return new MessageTypeSerializerImpl<>(
                messageTypeClass,
                generateByteSizeFunction(serializer, components),
                generateReader(constructor),
                generateWriter(components)
        );
    }

    /**
     * Generates a function implementation that counts the byte size of a
     * message based on the message's record component types.
     * @param serializer The serializer context to generate a function for.
     * @param components The list of components that make up the message.
     * @param <T> The message type.
     * @return A function that computes the byte size of a message of the given
     * type.
     */
    private static <T extends Message> Function<T, Integer> generateByteSizeFunction(Serializer serializer, RecordComponent[] components) {
        return msg -> {
            int size = 0;
            for (var component : components) {
                try {
                    size += MessageUtils.getByteSize(serializer, component.getAccessor().invoke(msg));
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            }
            return size;
        };
    }

    /**
     * Generates a message reader for the given message constructor method. It
     * will try to read objects from the input stream according to the
     * parameters of the canonical constructor of a message record.
     * @param constructor The canonical constructor of the message record.
     * @param <T> The message type.
     * @return A message reader for the given type.
     */
    private static <T extends Message> MessageReader<T> generateReader(Constructor<T> constructor) {
        return in -> {
            Object[] values = new Object[constructor.getParameterCount()];
            for (int i = 0; i < values.length; i++) {
                values[i] = in.readObject(constructor.getParameterTypes()[i]);
            }
            try {
                return constructor.newInstance(values);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    /**
     * Generates a message writer for the given message record components.
     * @param components The record components to write.
     * @param <T> The type of message.
     * @return The message writer for the given type.
     */
    private static <T extends Message> MessageWriter<T> generateWriter(RecordComponent[] components) {
        return (msg, out) -> {
            for (var component: components) {
                try {
                    out.writeObject(component.getAccessor().invoke(msg), component.getType());
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
