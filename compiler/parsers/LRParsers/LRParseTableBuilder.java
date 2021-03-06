package compiler.parsers.LRParsers;

import compiler.Rule;
import compiler.grammar.Grammar;
import compiler.parsers.LRParsers.items.Item;
import compiler.parsers.LRParsers.parsing_table.*;
import compiler.parsers.LRParsers.items.*;
import compiler.sets.*;

import java.util.*;

public abstract class LRParseTableBuilder {
    protected ParsingTable table;
    protected final Grammar grammar;
    Map<ItemSet, Integer> configuratingSets;

    protected Map<Integer, Map<String, Integer>> successors;

    /**
     * Generates an LR parse table given a {@link Grammar}
     * @param grammar the grammar to use
     */
    protected LRParseTableBuilder(Grammar grammar){
        this.grammar = grammar;
        generateConfiguratingSets();
        generateParsingTable();
    }

    /** Generates the parsing table */
    protected void generateParsingTable(){
        System.out.println("Generating parsing table entries...");

        table = new ParsingTable(configuratingSets.size());

        int i = 0;
        for(Map.Entry<ItemSet, Integer> entry : configuratingSets.entrySet()){
            ItemSet itemSet = entry.getKey();
            int state = entry.getValue();

            // Generate Action table
            generateActionSetEntries(state, itemSet);

            // Generate Goto table

            generateGotoSetEntries(state, itemSet);
            
            // Debug

            System.out.println("Generated parsing table entries for " + (++i) + " states (currently on state " + state + ")");
        }
    }

    protected Item getStartItem(){
        return new Item(grammar.getStartRule(), 0, new ComparableHashSet<>("__END__"));
    }

    /** Compute all configurating sets */
    protected void generateConfiguratingSets(){
        System.out.println("Generating configurating sets...");

        configuratingSets = new TreeMap<>();

        successors = new TreeMap<>();
        
        ItemSet initialState = closure(getStartItem());
        configuratingSets.put(initialState, 0);
        successors.put(0, new TreeMap<>());

        Set<ItemSet> edge = new TreeSet<>(Collections.singletonList(initialState));

        boolean updated = true;
        while(updated){
            updated = false;

            Set<ItemSet> newEdge = new TreeSet<>();

            for(ItemSet configuratingSet : edge){
                int state1 = configuratingSets.get(configuratingSet);
                for(String symbol : grammar.getAllSymbols()){
                    ItemSet successor = successor(configuratingSet, symbol);
                    if(successor.isEmpty()) continue;

                    if(addConfiguratingState(state1, symbol, successor)){
                        updated = true;
                        newEdge.add(successor);
                    }
                }
            }

            edge = newEdge;
        }
    }

    /** Tries to add a configurating set to the family of configurating sets and returns true if something was updated */
    protected boolean addConfiguratingState(int state, String symbol, ItemSet successor){
        if(!configuratingSets.containsKey(successor)){
            int newState = configuratingSets.size();
            successors.put(newState, new TreeMap<>());
            configuratingSets.put(successor, newState);
            successors.get(state).put(symbol, newState);
            System.out.println("Found " + configuratingSets.size() + "th configurating set (" + successor.size() + " items)");
            return true;
        }
        else{
            successors.get(state).put(symbol, configuratingSets.get(successor));
            return false;
        }
    }

    /** Generates the action table entries for a state */
    protected void generateActionSetEntries(int state, ItemSet itemSet){
        for(Item item : itemSet){
            if(item.isFinished() && item.getRule().equals(grammar.getStartRule())){
                table.setActionAccept(state, "__END__");
            }
            else if(item.isFinished()){
                generateReductions(state, item);
            }
            else{
                generateShifts(state, item);
            }
        }
    }

    /** Generate reduction entries given a state and an item */
    protected void generateReductions(int state, Item item){
        Rule reduce = item.getRule();
        for(String symbol : item.getLookahead()){
            table.setActionReduce(state, symbol, reduce);
        }
    }

    /** Generate shift entries given a state and an item */
    protected void generateShifts(int state, Item item){
        Integer nextState = successors.get(state).get(item.next());
        if(nextState != null){
            table.setActionShift(state, item.next(), nextState);
        }
    }

    /** Generates the goto table entries for a state */
    protected void generateGotoSetEntries(int state, ItemSet itemSet){
        for(String symbol : grammar.getNonTerminals()){
            Integer nextState = successors.get(state).get(symbol);
            if(nextState != null){
                table.setGoto(state, symbol, nextState);
            }
        }
    }

    /** Computes the closure of an item */
    protected abstract ItemSet closure(Item item);

    /** Computes the closure of an itemset */
    protected ItemSet closure(ItemSet itemSet){
        Set<Item> addedElements = new TreeSet<>();

        for(Item item : itemSet)
            addedElements.addAll(closure(item));

        itemSet.addAll(addedElements);

        return itemSet;
    }

    /** Computes the successor set of an itemset */
    protected ItemSet successor(ItemSet itemSet, String symbol){
        ItemSet res = new ItemSet();
        for(Item item : itemSet)
            if(!item.isFinished() && item.next().equals(symbol))
                res.add(item.shift());
        return closure(res);
    }

    /** Get the parsing table */
    public ParsingTable getTable(){
        return table;
    }
}
