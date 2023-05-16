package compiler.frontend.parsers.LRParsers.parsing_table;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import compiler.frontend.Rule;
import compiler.frontend.Symbol;

public class ParsingTable{
    
    private final List<Map<Symbol,TableEntry>> actionTable, gotoTable;
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
    
    public TableEntry getAction(int state, Symbol symbol){
        return actionTable.get(state).get(symbol);
    }

    public Set<Symbol> acceptableSymbolsAtState(int state) {
        return actionTable.get(state).keySet();
    }
    
    public TableEntry getGoto(int state, Symbol symbol){
        return gotoTable.get(state).get(symbol);
    }
    
    public void setActionReduce(int state, Symbol symbol, Rule rule){
        actionTable.get(state).put(symbol, new ReduceEntry(rule));
    }
    public void setActionShift(int state, Symbol symbol, int nextState){
        actionTable.get(state).put(symbol, new ShiftEntry(nextState));
    }
    public void setActionAccept(int state, Symbol symbol){
        actionTable.get(state).put(symbol, new AcceptEntry());
    }
    public void setGoto(int state, Symbol symbol, int n){
        gotoTable.get(state).put(symbol, new GotoEntry(n));
    }

    public void saveToFile(String filename){
        try(PrintWriter printWriter = new PrintWriter(filename)) {

            StringBuilder sb = new StringBuilder();

            sb.append(numStates);

            for(int state = 0; state < numStates; state++){
                for(Map.Entry<Symbol, TableEntry> mapEntry : actionTable.get(state).entrySet()){
                    Symbol symbol = mapEntry.getKey();
                    TableEntry entry = mapEntry.getValue();
                    sb.append("\na ");
                    sb.append(symbol);
                    switch (entry.getAction()) {
                        case SHIFT -> {
                            sb.append(" s ");
                            sb.append(((ShiftEntry) entry).nextState());
                        }
                        case ACCEPT -> sb.append(" a ");
                        case REDUCE -> {
                            sb.append(" r ");
                            Rule rule = ((ReduceEntry) entry).rule();
                            sb.append(rule.getRhsSize());
                            sb.append(" ");
                            sb.append(rule.getLhs());
                            sb.append(" ");
                            sb.append(rule.getRhs().toString());
                        }
                    }
                }
                for(Map.Entry<Symbol, TableEntry> mapEntry : gotoTable.get(state).entrySet()){
                    Symbol symbol = mapEntry.getKey();
                    GotoEntry entry = (GotoEntry) mapEntry.getValue();
                    sb.append("\ng ");
                    sb.append(symbol);
                    sb.append(" ");
                    sb.append(entry.nextState());
                }
                sb.append("\ns");
            }

            printWriter.print(sb);
            printWriter.flush();
        }
        catch(Exception e){
            System.out.println("Error saving LR parse table to file");
        }
    }

    public static ParsingTable loadFromFile(Symbol.SymbolTable symbolTable, String filename){
        try(Scanner scan = new Scanner(new File(filename))){
            int size = scan.nextInt();
            ParsingTable table = new ParsingTable(size);
            for(int state = 0; state < size; state++){
                String entryType;
                while(!(entryType = scan.next()).equals("s")){
                    Symbol symbol = symbolTable.get(scan.next());
                    switch (entryType) {
                        case "a" -> {
                            switch (scan.next()) {
                                case "a" -> table.setActionAccept(state, symbol);
                                case "s" -> table.setActionShift(state, symbol, scan.nextInt());
                                case "r" -> {
                                    int rhsSize = scan.nextInt();
                                    Symbol lhs = symbolTable.get(scan.next());
                                    Symbol[] rhs = new Symbol[rhsSize];
                                    for (int i = 0; i < rhsSize; i++) {
                                        var a = scan.next();
                                        try {
                                            rhs[i] = symbolTable.get(a);
                                        }
                                        catch (Error e){
                                            throw new Error("Could not get symbol " + a + " at state " + state);
                                        }
                                    }
                                    table.setActionReduce(state, symbol, new Rule(lhs, rhs));
                                }
                            }
                        }
                        case "g" -> table.setGoto(state, symbol, scan.nextInt());
                    }
                }
            }
            return table;
        }
        catch(Exception e){
            System.out.println("Error loading LR parse table from file");
            e.printStackTrace();
            return null;
        }
    }
}
