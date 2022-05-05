package compiler.parsingTable;

public interface TableEntry{
    public static enum Action { SHIFT, REDUCE, ACCEPT, GOTO }
    public Action getAction();
}