package compiler.parsers;

import java.util.Map;
import java.util.Map.Entry;

import compiler.Grammar;
import compiler.Rule;
import compiler.parsingTable.ParsingTable;

import java.util.TreeMap;

public class LR0Parser extends Parser{ 

    public LR0Parser(Grammar grammar){
        super(grammar);
    }

    public Map<ItemSet, Integer> generateConfiguratingSets(Grammar grammar){
        Map<ItemSet, Integer> configuratingSets = new TreeMap<>();
        ItemSet initialState = new Item(grammar, grammar.getStartRule(), 0).closure();
        configuratingSets.put(initialState, 0);
        
        boolean updated = true;
        while(updated){
            updated = false;

            Map<ItemSet,Integer> newSet = new TreeMap<>(configuratingSets);
            for(ItemSet configuratingSet : configuratingSets.keySet()){
                for(String symbol : grammar.getAllSymbols()){
                    ItemSet successor = configuratingSet.successor(symbol);
                    if(!successor.isEmpty() && !newSet.containsKey(successor)){
                        updated = true;
                        newSet.put(successor, newSet.size());
                    }
                }
            }

            configuratingSets = newSet;
        }
        return configuratingSets;
    }

    @Override
    public ParsingTable generateParsingTable(Grammar grammar){
        Map<ItemSet, Integer> configuratingSets = generateConfiguratingSets(grammar);

        table = new ParsingTable(configuratingSets.size());

        for(Entry<ItemSet, Integer> entry : configuratingSets.entrySet()){
            ItemSet itemSet = entry.getKey();
            int state = entry.getValue();

            // Generate Action table
            for(Item item : itemSet){
                generateActionSetEntry(configuratingSets, grammar, state, itemSet, item);
            }
            
            // Generate Goto table
            for(String symbol : grammar.getNonTerminals()){
                Integer nextState = configuratingSets.get(itemSet.successor(symbol));
                if(nextState != null) table.setGoto(state, symbol, nextState);
            }
        }

        return table;
    }

    public void generateActionSetEntry(Map<ItemSet, Integer> configuratingSets, Grammar grammar, int state, ItemSet itemSet, Item item){
        if(item.isFinished() && item.getRule().equals(grammar.getStartRule())){
            table.setActionAccept(state, "__END__");
        }
        else if(item.isFinished()){
            Rule reduce = item.getRule();
            for(String symbol : grammar.follow(reduce.getLhs())){
                table.setActionReduce(state, symbol, reduce);
            }
        }
        else{
            Integer st2 = configuratingSets.get(itemSet.successor(item.next()));
            if(st2 != null) table.setActionShift(state, item.next(), st2);
        }
    }
}