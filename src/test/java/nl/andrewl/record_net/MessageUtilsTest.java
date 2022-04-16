package nl.andrewl.record_net;

import nl.andrewl.record_net.msg.ChatMessage;
import org.junit.jupiter.api.Test;

import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageUtilsTest {
    @Test
    public void testGetByteSize() {
        assertEquals(4, MessageUtils.getByteSize((String) null));
        assertEquals(5, MessageUtils.getByteSize("a"));
        assertEquals(16, MessageUtils.getByteSize("Hello world!"));
        assertEquals(8, MessageUtils.getByteSize("", ""));
        assertEquals(10, MessageUtils.getByteSize("a", "b"));
        Message msg = new ChatMessage("andrew", 123, "Hello world!");
        int expectedMsgSize = 1 + 4 + 6 + 8 + 4 + 12;
        Serializer serializer = new Serializer();
        serializer.registerType(1, ChatMessage.class);
        assertEquals(1, MessageUtils.getByteSize(serializer, (Message) null));
        assertEquals(expectedMsgSize, MessageUtils.getByteSize(serializer, msg));
        assertEquals(4 * expectedMsgSize, MessageUtils.getByteSize(serializer, msg, msg, msg, msg));
        assertEquals(16, MessageUtils.getByteSize(serializer, UUID.randomUUID()));
        assertEquals(4, MessageUtils.getByteSize(serializer, StandardCopyOption.ATOMIC_MOVE));
    }
}
