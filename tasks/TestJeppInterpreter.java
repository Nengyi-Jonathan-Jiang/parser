import jepp.interpreter.Interpreter;

import java.io.InputStream;

public class TestJeppInterpreter {
    public static void main(String[] args) {
        String input;
        try (InputStream jeppFile = TestJeppInterpreter.class.getResourceAsStream("/test/fibonacci.jepp")) {
            assert jeppFile != null;
            input = new String(jeppFile.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Interpreter interpreter = new Interpreter(System.in, System.out);
        interpreter.run(input);
    }
}