package compiler.parsers;

import compiler.Rule;
import compiler.grammar.Grammar;
import compiler.items.Item;
import compiler.items.ItemSet;
import compiler.parsing_table.*;
import compiler.sets.*;

import java.util.*;

public abstract class LRParseTableBuilder {
    protected ParsingTable table;
    protected final Grammar grammar;

    protected Map<Integer, Map<String, Integer>> successors;

    /**
     * Generates an LR parse table given a {@link Grammar}
     * @param grammar the grammar to use
     */
    protected LRParseTableBuilder(Grammar grammar){
        this.grammar = grammar;
        generateParsingTable();
    }

    protected void generateParsingTable(){
        Map<ItemSet, Integer> configuratingSets = generateConfiguratingSets();

        System.out.println("Generating parsing table entries...");

        table = new ParsingTable(configuratingSets.size());

        int i = 0;
        for(Map.Entry<ItemSet, Integer> entry : configuratingSets.entrySet()){
            ItemSet itemSet = entry.getKey();
            int state = entry.getValue();

            System.out.println("Generated entries for " + (++i) + " states (currently on state " + state + ")");

            // Generate Action table
            for(Item item : itemSet){
                generateActionSetEntry(state, item);
            }

            // Generate Goto table
            for(String symbol : grammar.getNonTerminals()){
                Integer nextState = successors.get(state).get(symbol);
                if(nextState != null) table.setGoto(state, symbol, nextState);
            }
        }
    }

    protected Map<ItemSet, Integer> generateConfiguratingSets(){
        System.out.println("Generating configurating sets...");
        Map<ItemSet, Integer> configuratingSets = new TreeMap<>();
        successors = new TreeMap<>();
        ItemSet initialState = closure(new Item(grammar.getStartRule(), 0, new ComparableHashSet<>("__END__")));
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

                    int state2;

                    if(!configuratingSets.containsKey(successor)){
                        state2 = configuratingSets.size();
                        successors.put(state2, new TreeMap<>());
                        updated = true;
                        configuratingSets.put(successor, state2);
                        newEdge.add(successor);
                        System.out.println("Found " + (state2 + 1) + "th configurating set (" + successor.size() + " items)");
                    }
                    else state2 = configuratingSets.get(successor);

                    successors.get(state1).put(symbol, state2);
                }
            }

            edge = newEdge;
        }

        return configuratingSets;
    }

    protected void generateActionSetEntry(int state, Item item){
        if(item.isFinished() && item.getRule().equals(grammar.getStartRule())){
            table.setActionAccept(state, "__END__");
        }
        else if(item.isFinished()){
            Rule reduce = item.getRule();
            for(String symbol : item.getLookahead()){
                table.setActionReduce(state, symbol, reduce);
            }
        }
        else{
            Integer nextState = successors.get(state).get(item.next());
            if(nextState != null) table.setActionShift(state, item.next(), nextState);
        }


    }

    /**
     * Computes the closure of an item. Will be implemented
     * differently depending on the table generation algorithm
     */
    protected abstract ItemSet closure(Item item);

    protected ItemSet closure(ItemSet itemSet){
        Set<Item> addedElements = new TreeSet<>();

        for(Item item : itemSet)
            addedElements.addAll(closure(item));

        itemSet.addAll(addedElements);

        return itemSet;
    }

    protected ItemSet successor(ItemSet itemSet, String symbol){
        ItemSet res = new ItemSet();
        for(Item item : itemSet)
            if(!item.isFinished() && item.next().equals(symbol))
                res.add(item.shift());
        return closure(res);
    }

    public ParsingTable getTable(){
        return table;
    }
}
