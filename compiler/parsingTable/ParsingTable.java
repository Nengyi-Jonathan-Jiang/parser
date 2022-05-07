package compiler.parsingTable;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import compiler.Rule;

public class ParsingTable{
    
    private final List<Map<String,TableEntry>> actionTable, gotoTable;
    private final int numStates;
    
    public ParsingTable(int numStates){
        this.numStates = numStates;
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

    public void saveToFile(String filename){
        try {
            PrintWriter printWriter = new PrintWriter(filename);

            StringBuilder sb = new StringBuilder();

            sb.append(numStates);

            for(int state = 0; state < numStates; state++){
                for(Map.Entry<String, TableEntry> mapEntry : actionTable.get(state).entrySet()){
                    String symbol = mapEntry.getKey();
                    TableEntry entry = mapEntry.getValue();
                    sb.append("\na ");
                    sb.append(symbol);
                    switch(entry.getAction()){
                        case SHIFT:
                            sb.append(" s ");
                            sb.append(((ShiftEntry)entry).getNextState());
                            break;
                        case ACCEPT:
                            sb.append(" a ");
                            break;
                        case REDUCE:
                            sb.append(" r ");
                            Rule rule = ((ReduceEntry)entry).getRule();
                            sb.append(rule.getRhsSize());
                            sb.append(" ");
                            sb.append(rule.getLhs());
                            sb.append(" ");
                            sb.append(rule.getRhs().toString());
                            break;
                    }
                }
                for(Map.Entry<String, TableEntry> mapEntry : gotoTable.get(state).entrySet()){
                    String symbol = mapEntry.getKey();
                    GotoEntry entry = (GotoEntry) mapEntry.getValue();
                    sb.append("\ng ");
                    sb.append(symbol);
                    sb.append(" ");
                    sb.append(entry.getNextState());
                }
                sb.append("\ns");
            }

            printWriter.print(sb.toString());
            printWriter.flush();
            printWriter.close();
        }
        catch(Exception e){
            System.out.println("Error saving LR parse table to file");
        }
    }

    public static ParsingTable loadFromFile(String filename){
        try{
            Scanner scan = new Scanner(new File(filename));
            int size = scan.nextInt();
            ParsingTable table = new ParsingTable(size);
            for(int state = 0; state < size; state++){
                String tableType;
                while(!(tableType = scan.next()).equals("s")){
                    String symbol = scan.next();
                    switch(tableType){
                        case "a":
                            switch(scan.next()){
                                case "a":
                                    table.setActionAccept(state, symbol);
                                    break;
                                case "s":
                                    table.setActionShift(state, symbol, scan.nextInt());
                                    break;
                                case "r":
                                    int rhsSize = scan.nextInt();
                                    String lhs = scan.next();
                                    String[] rhs = new String[rhsSize];
                                    for(int i = 0; i < rhsSize; i++) rhs[i] = scan.next();
                                    table.setActionReduce(state, symbol, new Rule(lhs, rhs));
                                    break;
                            }
                            break;
                        case "g":
                            table.setGoto(state, symbol, scan.nextInt());
                            break;
                    }
                }
            }
            scan.close();
            return table;
        }
        catch(Exception e){
            System.out.println("Error loading LR parse table from file");
            e.printStackTrace();
            return null;
        }
    }
}
