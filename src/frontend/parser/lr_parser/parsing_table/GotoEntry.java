package frontend.parser.lr_parser.parsing_table;

import util.SerializableToString;

public record GotoEntry(int nextState) implements SerializableToString {

    @Override
    public void serializeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(" ");
        stringBuilder.append(nextState);
    }
}