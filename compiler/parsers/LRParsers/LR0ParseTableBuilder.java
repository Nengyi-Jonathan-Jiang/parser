package compiler.parsers.LRParsers;

import java.util.*;

import compiler.Rule;
import compiler.Symbol;
import compiler.grammar.Grammar;
import compiler.parsers.LRParsers.items.Item;
import compiler.parsers.LRParsers.items.ItemSet;
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
        Queue<Rule> dq = new ArrayDeque<>(Collections.singletonList(new Rule(grammar.symbolTable.__DUMMY__, item.next())));
        
        while(dq.size() > 0){
            Rule rule = dq.remove();
            if(rule.isEmpty()) continue;
            Symbol sym = rule.getRhs().firstTkn();
            
            if(!grammar.isNonTerminal(sym)) continue;
            
            for(Rule r : grammar.getRules(sym))
                if(res.add(new Item(r, 0, new ComparableTreeSet<>(grammar.getTerminals()))))
                    dq.add(r);
        }
        return res;
    }
}