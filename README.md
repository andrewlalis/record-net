# record-net
Simple, performant message library for Java, using records.

record-net gives you the advantages of reflection, without the runtime costs. By registering message types before starting your work, record-net is able to generate custom serializers and deserializers for all registered message types, which translates to read and write speeds that are nearly equivalent to directly writing bytes to a stream.

Here's an example of how you can use record-net:

```java
import nl.andrewl.record_net.Message;
import nl.andrewl.record_net.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

class Example {
    record ChatMessage(
            long timestamp,
            String username,
            String msg
    ) implements Message {}

    public static void main(String[] args) throws IOException {
        var ser = new Serializer();
        ser.registerType(1, ChatMessage.class);
        var socket = new Socket("127.0.0.1", 8081);
        var bOut = new ByteArrayOutputStream();
        var msg = new ChatMessage(
                System.currentTimeMillis(),
                "andrew",
                "Hello world!"
        );
        ser.writeMessage(msg, socket.getOutputStream());
        ChatMessage response = (ChatMessage) ser.readMessage(socket.getInputStream());
    }
}
```

## Get record-net
This project is published as a package on GitHub. You can view available packages [here](https://github.com/andrewlalis/record-net/packages).
