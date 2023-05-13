package compiler.parsers.LRParsers.parsing_table;

import compiler.Rule;

public record ReduceEntry(Rule rule) implements TableEntry {
    public Action getAction() {
        return Action.REDUCE;
    }
}