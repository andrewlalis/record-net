package nl.andrewl.record_net.msg;

import nl.andrewl.record_net.Message;

public record ChatMessage(
        String username,
        long timestamp,
        String message
) implements Message {}
