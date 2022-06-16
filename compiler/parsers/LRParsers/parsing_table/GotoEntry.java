package compiler.parsers.LRParsers.parsing_table;

public class GotoEntry implements TableEntry{
    private final int nextState;
    public GotoEntry(int nextState){this.nextState = nextState;}
    public int getNextState(){return nextState;}
    public Action getAction(){return Action.GOTO;}
}