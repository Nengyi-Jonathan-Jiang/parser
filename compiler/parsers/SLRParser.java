package compiler.parsers;

import java.util.*;

import compiler.Grammar;
import compiler.Rule;
import compiler.items.Item;
import compiler.items.ItemSet;
import compiler.ComparableSet;

public class SLRParser extends LRParser{ 

    public SLRParser(Grammar grammar){
        super(grammar);
    }

    public ItemSet closure(Item item){
        //The closure will always contain this item
        ItemSet res = new ItemSet(Arrays.asList(item));
        if(item.isFinished()) return res;
        //Now compute the rest of the closure
        Queue<Rule> dq = new ArrayDeque<>(Arrays.asList(new Rule("__DUMMY__", item.next())));
        
        while(dq.size() > 0){
            Rule rule = dq.remove();
            String sym = rule.getRhs().firstTkn();
            
            if(!grammar.isNonTerminal(sym)) continue;
            
            for(Rule r : grammar.getRules(sym))
                if(res.add(new Item(grammar, r, 0, new ComparableSet<>(grammar.follow(r.getLhs())))))
                    dq.add(r);
        }
        return res;
    }
}