package compiler.frontend.parsers.LRParsers.parsing_table;

public record GotoEntry(int nextState) implements TableEntry {
    public Action getAction() {
        return Action.GOTO;
    }
}