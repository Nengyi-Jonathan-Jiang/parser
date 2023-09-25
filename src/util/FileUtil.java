package util;

import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    private FileUtil(){}

    public static InputStream getInputStream(String file) {
        return FileUtil.class.getResourceAsStream("/" + file);
    }

    public static String getTextContents(String file) {
        try {
            return new String(getInputStream(file).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
