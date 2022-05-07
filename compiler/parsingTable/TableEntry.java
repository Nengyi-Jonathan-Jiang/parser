package compiler.parsingTable;

public interface TableEntry{
    enum Action { SHIFT, REDUCE, ACCEPT, GOTO }
    Action getAction();
}