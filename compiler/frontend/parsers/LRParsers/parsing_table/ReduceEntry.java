package compiler.frontend.parsers.LRParsers.parsing_table;

import compiler.frontend.Rule;

public record ReduceEntry(Rule rule) implements TableEntry {
    public Action getAction() {
        return Action.REDUCE;
    }
}