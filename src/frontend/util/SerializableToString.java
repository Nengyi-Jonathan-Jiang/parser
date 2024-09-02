package frontend.util;

public interface SerializableToString {
    void writeToStringBuilder(StringBuilder stringBuilder);

    default String serializeToString() {
        StringBuilder stringBuilder = new StringBuilder();
        writeToStringBuilder(stringBuilder);
        return stringBuilder.toString();
    }
}