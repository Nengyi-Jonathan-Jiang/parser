package parser;

import java.util.*;

import parser.grammar.Grammar;
import parser.grammar.Item;
import parser.grammar.ItemSet;
import parser.parsingTable.*;

public class LR0Parser{
    private class State implements Comparable<State>, Printable{    //A State is just an ItemSet + an integer id
        public int id;
        public ItemSet set;
        public State(ItemSet set, int id){
            this.id = id;
            this.set = set;
        }
        
        public int compareTo(State other){return set.compareTo(other.set);}
        public boolean equals(State other){return set.equals(other.set);}
        public String toString(){return "State " + id + " " + set;}
    }
    
    Map<ItemSet,State> configuratingSets;

    ParsingTable parsingTable;
    
    public LR0Parser(Grammar grammar){
        
        //Construct configurating sets / states and state transition table
        
        String startSymbol = grammar.startSymbol;
        Rule startRule = grammar.getRules(startSymbol).iterator().next();
        
        Item endItem = new Item(grammar, startRule, startRule.getRhs().size());

        configuratingSets = new TreeMap<>();
        
        State initialState = new State(new Item(grammar, startRule,0).closure(), 0);
        configuratingSets.put(initialState.set, initialState);
        
        boolean updated = true;
        while(updated){
            updated = false;
            Map<ItemSet,State> newSet = new TreeMap<>(configuratingSets);
            for(State cfs : configuratingSets.values()){
                for(String sym : grammar.allSymbols){
                    ItemSet set = cfs.set.successor(sym);
                    if(set.size() != 0 && !newSet.containsKey(set)){
                        updated = true;
                        newSet.put(set, new State(set, newSet.size()));
                    }
                }
            }
            configuratingSets = newSet;
        }
        
        //Construct parsing table
        
        parsingTable = new ParsingTable(configuratingSets.size());
        
        for(State st : configuratingSets.values()){
            int i = st.id;
            ItemSet its = st.set;

            for(Item it : its){
                if(it.equals(endItem)){
                    parsingTable.setActionAccept(i, "__END__");
                }
                else if(it.isFinished()){
                    Rule reduce = it.getRule();
                    for(String sym : grammar.follow(reduce.getLhs())){
                        parsingTable.setActionReduce(i, sym, reduce);
                    }
                }
                else{
                    State st2 = configuratingSets.get(its.successor(it.next()));
                    if(st2 != null) parsingTable.setActionShift(i, it.next(), st2.id);
                }
            }
            
            for(String sym : grammar.nonTerminals){
                State st2 = configuratingSets.get(its.successor(sym));
                if(st2 != null) parsingTable.setGoto(i, sym, st2.id);
            }
        }
        
        // System.out.println(Main.prnt(configuratingSets.keySet()));

        // Print out the tables
        
        if(Math.sqrt(0) != 0)
        {
            for(int i = 0; i < configuratingSets.size(); i++){
                for(String sym : grammar.terminals){
                    TableEntry entry = parsingTable.getAction(i, sym);
                    if(entry == null) continue;
                    switch(entry.getAction()){
                        case ACCEPT:
                            System.out.print("Action[" + i + "][\"" + sym + "\"] = ");
                            System.out.println("ACCEPT");
                            break;
                        case SHIFT:
                            System.out.print("Action[" + i + "][\"" + sym + "\"] = ");
                            System.out.print("SHIFT ");
                            System.out.println(((ShiftEntry)entry).getNextState());
                            break;
                        case REDUCE:
                            System.out.print("Action[" + i + "][\"" + sym + "\"] = ");
                            System.out.print("REDUCE ");
                            System.out.println(((ReduceEntry)entry).getRule());
                            break;
                        case GOTO: break;
                    }
                }
                for(String sym : grammar.nonTerminals){
                    GotoEntry gotoEntry = (GotoEntry)parsingTable.getGoto(i, sym);
                    if(gotoEntry == null) continue;
                    System.out.print("Goto[" + i + "][\"" + sym + "\"] = ");
                    System.out.println(gotoEntry.getNextState());
                }
            }
        }
    }

    public void parse(String[] tokens){
        String[] tkns = new String[tokens.length + 1];
        System.arraycopy(tokens, 0, tkns, 0, tokens.length);
        tkns[tokens.length] = "__END__";

        Deque<Integer> stateStack = new ArrayDeque<>();
        Deque<String> tknStack = new ArrayDeque<>();
        stateStack.push(0);

        for(int i = 0; i < tkns.length; ){
            int state = stateStack.peek();
            String tkn = tkns[i];

            // System.out.println("Current state: " + state);
            // System.out.println("Current token: \"" + tkn + "\"");
            // System.out.println(stateStack);

            TableEntry entry = parsingTable.getAction(state, tkn);

            if(entry == null){
                System.out.println("Could not parse string!");
                return;
            }

            switch(entry.getAction()){
                case SHIFT:
                    stateStack.push(((ShiftEntry)entry).getNextState());
                    System.out.println("SHIFT \"" + tkn + "\"");
                    tknStack.push(tkn);
                    i++;
                    break;
                case ACCEPT:
                    System.out.println("ACCEPTED INPUT");
                    return;
                case REDUCE:
                    Rule reduceRule = ((ReduceEntry)entry).getRule();
                    System.out.println("REDUCE " + reduceRule);
                    String lhs = reduceRule.getLhs();
                    
                    for(int j = 0; j < reduceRule.getRhs().size(); j++){
                        stateStack.pop();
                        tknStack.pop();
                    }

                    // System.out.println(stateStack);

                    // System.out.println("Querying GOTO[" + state + "][\"" + lhs + "\"]");

                    GotoEntry gotoEntry = (GotoEntry)parsingTable.getGoto(stateStack.peek(), lhs);
                    stateStack.push(gotoEntry.getNextState());
                    tknStack.push(lhs);
                    break;
                case GOTO: break;
            }

            
            System.out.print(String.format("%-20s", tknStack.toString().replaceAll("^\\[|\\]$", "").replaceAll(", ", " ")));
        }
    }
}