package jepp.language;

public interface JeppValue {
    JeppType getType();
    JeppValue[] getData();
}
