package jepp.interpreter;

public class JeppInterpreterException extends RuntimeException {
    public JeppInterpreterException(String cannotSubtractStrings) {
        super(cannotSubtractStrings);
    }
}
