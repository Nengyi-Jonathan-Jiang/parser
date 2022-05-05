package parser.parsers;

import java.util.*;
import java.util.Map.Entry;

import parser.grammar.Grammar;
import parser.grammar.Item;
import parser.grammar.ItemSet;
import parser.parsingTable.*;
import parser.*;

public class LR0Parser{
    Map<ItemSet,Integer> configuratingSets;

    ParsingTable parsingTable;
    
    public LR0Parser(Grammar grammar){
        
        //Construct configurating sets / states and state transition table
        
        configuratingSets = generateConfiguratingSets(grammar);
        //Construct parsing table
        
        parsingTable = generateParsingTable(grammar);
    }

    public Map<ItemSet, Integer> generateConfiguratingSets(Grammar grammar){
        Map<ItemSet, Integer> configuratingSets = new TreeMap<>();
        ItemSet initialState = new Item(grammar, grammar.startRule, 0).closure();
        configuratingSets.put(initialState, 0);
        
        boolean updated = true;
        while(updated){
            updated = false;
            Map<ItemSet,Integer> newSet = new TreeMap<>(configuratingSets);
            for(ItemSet configuratingSet : configuratingSets.keySet()){
                for(String symbol : grammar.allSymbols){
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

    public ParsingTable generateParsingTable(Grammar grammar){
        ParsingTable table = new ParsingTable(configuratingSets.size());

        for(Entry<ItemSet, Integer> entry : configuratingSets.entrySet()){
            ItemSet itemSet = entry.getKey();
            int state = entry.getValue();

            for(Item item : itemSet){
                generateActionSetEntry(table, grammar, state, itemSet, item);
            }
            
            for(String symbol : grammar.nonTerminals){
                Integer nextState = configuratingSets.get(itemSet.successor(symbol));
                if(nextState != null) table.setGoto(state, symbol, nextState);
            }
        }

        return table;
    }

    public void generateActionSetEntry(ParsingTable table, Grammar grammar, int state, ItemSet itemSet, Item item){
        if(item.isFinished() && item.getRule().equals(grammar.startRule)){
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

    public void parse(String[] tokens){
        new Parser(parsingTable).parse(tokens);
    }
}