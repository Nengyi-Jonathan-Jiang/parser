package compiler.parsers;

import java.util.*;

import compiler.Rule;
import compiler.grammar.Grammar;
import compiler.items.Item;
import compiler.items.ItemSet;
import compiler.ComparableSet;

public class LR0Parser extends LRParser{ 

    public LR0Parser(Grammar grammar){
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
            if(rule.isEmpty()) continue;
            String sym = rule.getRhs().firstTkn();
            
            if(!grammar.isNonTerminal(sym)) continue;
            
            for(Rule r : grammar.getRules(sym))
                if(res.add(new Item(r, 0, new ComparableSet<>(grammar.getTerminals()))))
                    dq.add(r);
        }
        return res;
    }
}