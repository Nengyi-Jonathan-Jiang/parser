package compiler.parsers.LRParsers.parsing_table;

public interface TableEntry{
    enum Action { SHIFT, REDUCE, ACCEPT, GOTO }
    Action getAction();
}