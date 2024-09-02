package frontend.parser.lr_parser.parsing_table;

import frontend.util.SerializableToString;

public record GotoEntry(int nextState) implements SerializableToString {

    @Override
    public void writeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(" ");
        stringBuilder.append(nextState);
    }
}