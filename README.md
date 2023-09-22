# record-net
Simple, performant message library for Java, using records. It allows you to
serialize and deserialize records and their contents for use in network or
filesystem IO.

Add it to your Maven project like so:
```xml
<dependency>
    <groupId>com.andrewlalis</groupId>
    <artifactId>record-net</artifactId>
    <version>1.0.0</version>
</dependency>
```
or Gradle:
```groovy
implementation 'com.andrewlalis:record-net:1.0.0'
```

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

## Module System
If using the Java Platform Module System (JPMS), then you should add an
**opens** declaration to open any packages containing your records to the
record-net module. For example, suppose I have defined my serializable
records in `com.example.app.data`. Then my `module-info.java` might look like this:

```java
module com.example.app {
    // Require record-net as a dependency:
    requires com.andrewlalis.record_net;
    // Allow record-net to inspect our records:
    opens com.example.app.data to com.andrewlalis.record_net;
}
```

For more info on the module system, consult this helpful article:
https://www.oracle.com/corporate/features/understanding-java-9-modules.html
