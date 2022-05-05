package parser.parsingTable;

import java.util.*;

import parser.Rule;

public class ParsingTable{
    
    public List<Map<String,TableEntry>> actionTable, gotoTable;
    
    public ParsingTable(int numStates){
        actionTable = new ArrayList<>(numStates);
        gotoTable = new ArrayList<>(numStates);
        for(int i = 0; i < numStates; i++){
            actionTable.add(new TreeMap<>());
            gotoTable.add(new TreeMap<>());
        }
    }
    
    public TableEntry getAction(int state, String symbol){
        return actionTable.get(state).get(symbol);
    }
    
    public TableEntry getGoto(int state, String symbol){
        return gotoTable.get(state).get(symbol);
    }
    
    public void setActionReduce(int state, String symbol, Rule rule){
        actionTable.get(state).put(symbol, new ReduceEntry(rule));
    }
    public void setActionShift(int state, String symbol, int nextState){
        actionTable.get(state).put(symbol, new ShiftEntry(nextState));
    }
    public void setActionAccept(int state, String symbol){
        actionTable.get(state).put(symbol, new AcceptEntry());
    }
    public void setGoto(int state, String symbol, int n){
        gotoTable.get(state).put(symbol, new GotoEntry(n));
    }
}
