package nl.andrewl.record_net;

import nl.andrewl.record_net.util.ExtendedDataOutputStream;

import java.io.IOException;

@FunctionalInterface
public interface MessageWriter<T extends Message> {
	/**
	 * Writes this message to the given output stream.
	 * @param msg The message to write.
	 * @param out The output stream to write to.
	 * @throws IOException If an error occurs while writing.
	 */
	void write(T msg, ExtendedDataOutputStream out) throws IOException;
}
