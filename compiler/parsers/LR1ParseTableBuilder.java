package compiler.parsers;

import java.util.*;

import compiler.Rule;
import compiler.items.Item;
import compiler.items.ItemSet;
import compiler.ComparableSet;
import compiler.SymbolString;
import compiler.grammar.Grammar;

public class LR1ParseTableBuilder extends LRParseTableBuilder{

    public LR1ParseTableBuilder(Grammar grammar){
        super(grammar);
    }

    protected static Map<Item, ItemSet> memoization = new TreeMap<>();

    protected ItemSet closure(Item item){
        if(memoization.containsKey(item)) return memoization.get(item);

        ItemSet res = new ItemSet(Collections.singletonList(item));
        if(item.isFinished()) return res;
        
        boolean updated = true;
        while(updated){
            updated = false;

            ItemSet nRes = res.copy();
            
            for(Item itm : res){
                if(itm.isFinished()) continue;

                String symbol = itm.next();
                SymbolString rest = itm.getRule().getRhs().substr(itm.getPos() + 1);

                if(!grammar.isNonTerminal(symbol)) continue;
                
                for(Rule r : grammar.getRules(symbol)){
                    for(String lookahead : itm.getLookahead()){
                        updated |= nRes.add(new Item(r, 0, new ComparableSet<>(grammar.first(rest.concat(lookahead)))));
                    }
                }
            }

            res = nRes;
        }

        memoization.put(item, res);

        return res;
    }
}