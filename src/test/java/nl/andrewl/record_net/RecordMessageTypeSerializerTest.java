package nl.andrewl.record_net;

import nl.andrewl.record_net.msg.ChatMessage;
import nl.andrewl.record_net.util.ExtendedDataInputStream;
import nl.andrewl.record_net.util.ExtendedDataOutputStream;
import nl.andrewl.record_net.util.RecordMessageTypeSerializer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecordMessageTypeSerializerTest {
    @Test
    public void testGenerateForRecord() throws IOException {
        Serializer serializer = new Serializer();
        var s1 = RecordMessageTypeSerializer.get(serializer, ChatMessage.class);
        ChatMessage msg = new ChatMessage("andrew", 123, "Hello world!");
        int expectedByteSize = 4 + msg.username().length() + 8 + 4 + msg.message().length();
        assertEquals(expectedByteSize, s1.byteSizeFunction().apply(msg));

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ExtendedDataOutputStream eOut = new ExtendedDataOutputStream(serializer, bOut);
        s1.writer().write(msg, eOut);
        byte[] data = bOut.toByteArray();
        assertEquals(expectedByteSize, data.length);
        ChatMessage readMsg = s1.reader().read(new ExtendedDataInputStream(serializer, data));
        assertEquals(msg, readMsg);

        // Only record classes can be generated.
        class NonRecordMessage implements Message {}
        assertThrows(IllegalArgumentException.class, () -> RecordMessageTypeSerializer.get(serializer, NonRecordMessage.class));
    }
}
