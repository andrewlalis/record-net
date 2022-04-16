package nl.andrewl.record_net;

import nl.andrewl.record_net.msg.ChatMessage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializerTest {
    @Test
    public void testReadAndWriteMessage() throws IOException {
        Serializer s = new Serializer();
        s.registerType(1, ChatMessage.class);

        ChatMessage msg = new ChatMessage("andrew", 123, "Hello world!");

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        s.writeMessage(msg, bOut);
        byte[] data = bOut.toByteArray();
        assertEquals(MessageUtils.getByteSize(s, msg), data.length);
        assertEquals(data[0], 1);

        ChatMessage readMsg = (ChatMessage) s.readMessage(new ByteArrayInputStream(data));
        assertEquals(msg, readMsg);
    }
}
