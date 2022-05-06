package compiler.parsers;

import java.util.*;

import compiler.Rule;
import compiler.items.Item;
import compiler.items.ItemSet;
import compiler.ComparableSet;
import compiler.SymbolString;
import compiler.grammar.Grammar;

public class LR1Parser extends LRParser{ 

    public LR1Parser(Grammar grammar){
        super(grammar);
    }

    public ItemSet closure(Item item){
        //The closure will always contain this item
        ItemSet res = new ItemSet(Arrays.asList(item));
        if(item.isFinished()) return res;
        //Now compute the rest of the closure
        
        boolean updated = true;
        while(updated){
            updated = false;

            ItemSet nRes = res.copy();
            
            for(Item itm : res){
                if(itm.getRule().getRhsSize() == 0) continue;
                
                String symbol = itm.next();
                SymbolString rest = itm.getRule().getRhs().substr(itm.getPos() + 1);

                if(!grammar.isNonTerminal(symbol)) continue;
                
                for(Rule r : grammar.getRules(symbol)){
                    for(String lookahead : itm.getLookahead()){
                        updated |= nRes.add(new Item(grammar, r, 0, new ComparableSet<>(grammar.first(rest.concat(lookahead)))));
                    }
                }
            }

            res = nRes;
        }
        return res;
    }
}