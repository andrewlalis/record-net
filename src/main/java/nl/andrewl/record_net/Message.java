package nl.andrewl.record_net;

/**
 * Represents any message which can be sent over the network.
 * <p>
 *     All messages consist of a single byte type identifier, followed by a
 *     payload whose structure depends on the message.
 * </p>
 */
public interface Message {
	/**
	 * Convenience method to get the serializer for this message's type, using
	 * the static auto-generated set of serializers.
	 * @param <T> The message type.
	 * @return The serializer to use to read and write messages of this type.
	 */
	@SuppressWarnings("unchecked")
	default <T extends Message> MessageTypeSerializer<T> getTypeSerializer(Serializer serializer) {
		return (MessageTypeSerializer<T>) serializer.getTypeSerializer(this.getClass());
	}
}
