package compiler.frontend.parsers.LRParsers.parsing_table;

public class AcceptEntry implements TableEntry{
    public Action getAction(){return Action.ACCEPT;}
}