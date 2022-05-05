package parser.parsingTable;

public class GotoEntry implements TableEntry{
    private int nextState;
    public GotoEntry(int nextState){this.nextState = nextState;}
    public int getNextState(){return nextState;}
    public Action getAction(){return Action.GOTO;}
}