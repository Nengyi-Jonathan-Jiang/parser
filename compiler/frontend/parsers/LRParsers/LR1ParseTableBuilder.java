package compiler.frontend.parsers.LRParsers;

import compiler.frontend.Rule;
import compiler.frontend.Symbol;
import compiler.frontend.SymbolString;
import compiler.frontend.grammar.Grammar;
import compiler.frontend.parsers.LRParsers.items.Item;
import compiler.frontend.parsers.LRParsers.items.ItemSet;
import compiler.util.Cache;
import compiler.util.CompareCache;

public class LR1ParseTableBuilder extends LRParseTableBuilderBase {

    protected Cache<Item, ItemSet> memoization;

    public LR1ParseTableBuilder(Grammar grammar){
        super(grammar);
    }

    protected ItemSet closure(Item item){
        if(memoization == null) memoization = new CompareCache<>();

        {
            var cachedRes = memoization.get(item);
            if(cachedRes != null) return cachedRes;
        }

        ItemSet res = new ItemSet(item);

        if(item.isFinished()) return res;
        
        ItemSet edge = res.copy();
        
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
                        Item newItem = new Item(r, 0, grammar.first(rest.append(lookahead)));

                        updated |= res.add(newItem);
                        newEdge.add(newItem);
                    }
                }
            }

            edge = newEdge;
        }

        memoization.cache(item, res);

        return res;
    }
}