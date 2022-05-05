package compiler.parsingTable;

public class ShiftEntry implements TableEntry{
    private int nextState;
    public ShiftEntry(int nextState){this.nextState = nextState;}
    public int getNextState(){return nextState;}
    public Action getAction(){return Action.SHIFT;}
}
