package util.stringstream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class StringPrintStream extends PrintStream {
    private final ByteArrayOutputStream output;

    public StringPrintStream(){
        this(new ByteArrayOutputStream());
    }

    public StringPrintStream(ByteArrayOutputStream output) {
        super(output);
        this.output = output;
    }

    @Override
    public String toString() {
        try {
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return output.toString();
    }
}
