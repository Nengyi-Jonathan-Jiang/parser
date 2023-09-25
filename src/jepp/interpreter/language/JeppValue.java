package jepp.interpreter.language;

public interface JeppValue {
    JeppType getType();
    JeppValue[] getData();
    boolean isTruthy();
}
