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
        
        String startSymbol = grammar.startSymbol;
        Rule startRule = grammar.getRules(startSymbol).iterator().next();
        
        configuratingSets = new TreeMap<>();
        
        ItemSet initialState = new Item(grammar, startRule,0).closure();
        configuratingSets.put(initialState, 0);
        
        boolean updated = true;
        while(updated){
            updated = false;
            Map<ItemSet,Integer> newSet = new TreeMap<>(configuratingSets);
            for(ItemSet cfs : configuratingSets.keySet()){
                for(String sym : grammar.allSymbols){
                    ItemSet set = cfs.successor(sym);
                    if(!set.isEmpty() && !newSet.containsKey(set)){
                        updated = true;
                        newSet.put(set, newSet.size());
                    }
                }
            }
            configuratingSets = newSet;
        }
        
        //Construct parsing table
        
        parsingTable = generateParsingTable(grammar);
    }

    public ParsingTable generateParsingTable(Grammar grammar){
        ParsingTable table = new ParsingTable(configuratingSets.size());

        String startSymbol = grammar.startSymbol;
        Rule startRule = grammar.getRules(startSymbol).iterator().next();
        
        Item endItem = new Item(grammar, startRule, startRule.getRhs().size());

        for(Entry<ItemSet, Integer> entry : configuratingSets.entrySet()){
            ItemSet its = entry.getKey();
            int i = entry.getValue();

            for(Item it : its){
                if(it.equals(endItem)){
                    table.setActionAccept(i, "__END__");
                }
                else if(it.isFinished()){
                    Rule reduce = it.getRule();
                    for(String sym : grammar.follow(reduce.getLhs())){
                        table.setActionReduce(i, sym, reduce);
                    }
                }
                else{
                    Integer st2 = configuratingSets.get(its.successor(it.next()));
                    if(st2 != null) table.setActionShift(i, it.next(), st2);
                }
            }
            
            for(String sym : grammar.nonTerminals){
                Integer st2 = configuratingSets.get(its.successor(sym));
                if(st2 != null) table.setGoto(i, sym, st2);
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
            for(String sym : grammar.follow(reduce.getLhs())){
                table.setActionReduce(state, sym, reduce);
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