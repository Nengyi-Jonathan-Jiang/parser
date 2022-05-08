package compiler.parsers;

import java.util.*;

import compiler.Rule;
import compiler.grammar.Grammar;
import compiler.items.Item;
import compiler.items.ItemSet;
import compiler.sets.ComparableTreeSet;

public class LR0ParseTableBuilder extends LRParseTableBuilder{

    public LR0ParseTableBuilder(Grammar grammar){
        super(grammar);
    }

    protected ItemSet closure(Item item){
        //The closure will always contain this item
        ItemSet res = new ItemSet(Collections.singletonList(item));
        if(item.isFinished()) return res;
        //Now compute the rest of the closure
        Queue<Rule> dq = new ArrayDeque<>(Collections.singletonList(new Rule("__DUMMY__", item.next())));
        
        while(dq.size() > 0){
            Rule rule = dq.remove();
            if(rule.isEmpty()) continue;
            String sym = rule.getRhs().firstTkn();
            
            if(!grammar.isNonTerminal(sym)) continue;
            
            for(Rule r : grammar.getRules(sym))
                if(res.add(new Item(r, 0, new ComparableTreeSet<>(grammar.getTerminals()))))
                    dq.add(r);
        }
        return res;
    }
}