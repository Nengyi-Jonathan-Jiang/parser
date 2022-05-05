package compiler.parsingTable;

import compiler.Rule;

public class ReduceEntry implements TableEntry{
    private Rule rule;
    public ReduceEntry(Rule rule){this.rule = rule;}
    public Rule getRule(){return rule;}
    public Action getAction(){return Action.REDUCE;}
}