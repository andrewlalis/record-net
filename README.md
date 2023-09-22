# record-net
Simple, performant message library for Java, using records. It allows you to
serialize and deserialize records and their contents for use in network or
filesystem IO.

## Example

```java
import com.andrewlalis.record_net.RecordMappedSerializer;
import java.io.*;

class Main {
    public static void main(String[] args) throws IOException {
        record MyData(int a, float b, String s) {}
        record FileData(String name, byte[] data) {}
        
        // Register the record types you want to use.
        RecordMappedSerializer serializer = new RecordMappedSerializer();
        serializer.registerType(MyData.class);
        serializer.registerType(FileData.class);

        // Write records.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.writeMessage(new MyData(42, 3.1415f, "Hello world!"), out);
        serializer.writeMessage(new FileData("test.txt", new byte[]{1, 2, 3, 4}), out);
        byte[] serialized = out.toByteArray();

        // Read records.
        Object obj = serializer.readMessage(new ByteArrayInputStream(serialized));
        switch (obj) {
            case MyData d -> System.out.println("Got MyData: " + d);
            case FileData d -> System.out.println("Got file data: " + d);
            default -> throw new RuntimeException();
        }
    }
}
```
