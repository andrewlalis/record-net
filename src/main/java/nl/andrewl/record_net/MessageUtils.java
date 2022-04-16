package nl.andrewl.record_net;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Utility class which provides methods for serializing and deserializing complex
 * data types.
 */
public class MessageUtils {
	public static final int UUID_BYTES = 2 * Long.BYTES;
	public static final int ENUM_BYTES = Integer.BYTES;

	/**
	 * Gets the number of bytes that the given string will occupy when it is
	 * serialized.
	 * @param s The string. This may be null.
	 * @return The number of bytes used to serialize the string.
	 */
	public static int getByteSize(String s) {
		return Integer.BYTES + (s == null ? 0 : s.getBytes(StandardCharsets.UTF_8).length);
	}

	/**
	 * Gets the number of bytes that all the given strings will occupy when
	 * serialized with a length-prefix encoding.
	 * @param strings The set of strings.
	 * @return The total byte size.
	 */
	public static int getByteSize(String... strings) {
		int size = 0;
		for (var s : strings) {
			size += getByteSize(s);
		}
		return size;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Message> int getByteSize(Serializer serializer, T msg) {
		if (msg == null) {
			return 1;
		} else {
			MessageTypeSerializerImpl<T> typeSerializer = (MessageTypeSerializerImpl<T>) serializer.getTypeSerializer(msg.getClass());
			return 1 + typeSerializer.byteSizeFunction().apply(msg);
		}
	}

	public static <T extends Message> int getByteSize(Serializer serializer, T[] items) {
		int count = Integer.BYTES;
		for (var item : items) {
			count += getByteSize(serializer, item);
		}
		return count;
	}

	public static int getByteSize(Serializer serializer, Object o) {
		if (o instanceof Integer) {
			return Integer.BYTES;
		} else if (o instanceof Long) {
			return Long.BYTES;
		} else if (o instanceof String) {
			return getByteSize((String) o);
		} else if (o instanceof UUID) {
			return UUID_BYTES;
		} else if (o instanceof Enum<?>) {
			return ENUM_BYTES;
		} else if (o instanceof byte[]) {
			return Integer.BYTES + ((byte[]) o).length;
		} else if (o.getClass().isArray() && Message.class.isAssignableFrom(o.getClass().getComponentType())) {
			return getByteSize(serializer, (Message[]) o);
		} else if (o instanceof Message) {
			return getByteSize(serializer, (Message) o);
		} else {
			throw new IllegalArgumentException("Unsupported object type: " + o.getClass().getSimpleName());
		}
	}

	public static int getByteSize(Serializer serializer, Object... objects) {
		int size = 0;
		for (var o : objects) {
			size += getByteSize(serializer, o);
		}
		return size;
	}
}
