package compiler.parsers.LRParsers;

import java.util.*;

import compiler.grammar.Grammar;
import compiler.parsers.LRParsers.items.Item;
import compiler.parsers.LRParsers.items.ItemSet;
import compiler.sets.ComparableTreeSet;
import compiler.*;

public class SLRParseTableBuilder extends LRParseTableBuilder{

    public SLRParseTableBuilder(Grammar grammar){
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
                if(res.add(new Item(r, 0, new ComparableTreeSet<>(grammar.follow(r.getLhs())))))
                    dq.add(r);
        }
        return res;
    }
}