package compiler.parsers.LRParsers;

import java.util.*;

import compiler.*;
import compiler.sets.*;
import compiler.grammar.Grammar;
import compiler.parsers.LRParsers.items.Item;
import compiler.parsers.LRParsers.items.ItemSet;

public class LR1ParseTableBuilder extends AbstractLRParseTableBuilder {

    protected Map<Item, ItemSet> memoization;

    public LR1ParseTableBuilder(Grammar grammar){
        super(grammar);
    }

    protected ItemSet closure(Item item){
        if(memoization == null) memoization = new TreeMap<>();
        else if(memoization.containsKey(item)) return memoization.get(item);

        ItemSet res = new ItemSet(Collections.singletonList(item));

        if(item.isFinished()) return res;
        
        ItemSet edge = new ItemSet(res);
        
        boolean updated = true;
        while(updated){
            updated = false;

            ItemSet newEdge = new ItemSet();
            
            for(Item itm : edge){

                Symbol symbol = itm.next();

                if(itm.isFinished() || !grammar.isNonTerminal(symbol)) continue;

                SymbolString rest = itm.getRule().getRhs().substr(itm.getPos() + 1);

                for(Rule r : grammar.getRules(symbol)){
                    for(Symbol lookahead : itm.getLookahead()){
                        Item newItem = new Item(r, 0, new ComparableTreeSet<>(grammar.first(rest.append(lookahead))));

                        updated |= res.add(newItem);
                        newEdge.add(newItem);
                    }
                }
            }

            edge = newEdge;
        }

        memoization.put(item, res);

        return res;
    }
}