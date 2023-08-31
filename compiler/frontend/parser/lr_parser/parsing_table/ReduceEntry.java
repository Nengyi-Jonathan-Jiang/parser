package compiler.frontend.parser.lr_parser.parsing_table;

import compiler.frontend.parser.Rule;

public record ReduceEntry(Rule rule) implements TableEntry {
    public Action getAction() {
        return Action.REDUCE;
    }
}