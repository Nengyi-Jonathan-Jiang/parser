package util;

import java.io.ByteArrayInputStream;

public class StringInputStream extends ByteArrayInputStream {
    public StringInputStream(String str) {
        super(str.getBytes());
    }
}
