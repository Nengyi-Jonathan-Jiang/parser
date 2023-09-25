package util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StringInputStream extends ByteArrayInputStream {
    public StringInputStream(String str) {
        super(str.getBytes());
    }
}
