package compiler.frontend.parsers.LRParsers.parsing_table;

public record ShiftEntry(int nextState) implements TableEntry {
    public Action getAction() {
        return Action.SHIFT;
    }
}
