package util;

public interface SerializableToString {
    void serializeToStringBuilder(StringBuilder stringBuilder);

    default String serializeToString() {
        StringBuilder stringBuilder = new StringBuilder();
        serializeToStringBuilder(stringBuilder);
        return stringBuilder.toString();
    }
}