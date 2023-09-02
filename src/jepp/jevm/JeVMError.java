package jepp.jevm;

public class JeVMError extends Error {
    public JeVMError(String message) {
        super("JeVM Error: " + message);
    }
}