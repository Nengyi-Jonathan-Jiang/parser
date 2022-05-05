package parser.grammar;
import java.util.*;

import parser.Printable;
import parser.Rule;
import parser.ComparableSet;

public class Item implements Comparable<Item>, Printable{
    private final Rule rule;
    private final int pos;
    private final Grammar grammar;
    private final ComparableSet<String> lookahead;

    public Item(Grammar grammar, Rule rule, int pos){
        this(grammar, rule, pos, grammar.follow(rule.getLhs()));
    }
    public Item(Grammar grammar, Rule rule, int pos, ComparableSet<String> lookahead){
        this.rule = rule;
        this.pos = pos;
        this.grammar = grammar;
        this.lookahead = lookahead;
    }
    
    public Rule getRule(){return rule;}
    public int getPos(){return pos;}
    
    public boolean isFinished(){
        return pos >= rule.getRhs().size();
    }
    
    public String next(){
        if(isFinished()) return null;
        return rule.getRhs().get(pos);
    }
    
    public Item shift(){
        if(isFinished()) throw new IndexOutOfBoundsException();
        return new Item(grammar, rule, pos + 1);
    }
    
    public int compareTo(Item other){
        if(pos != other.pos) return pos - other.pos;
        return rule.compareTo(other.rule);
    }
    
    public boolean equals(Item other){
        return pos == other.pos && rule.equals(other.rule);
    }
    
    public ItemSet closure(){
        //The closure will always contain this item
        ItemSet res = new ItemSet(Arrays.asList(new Item[]{this}));
        if(isFinished()) return res;
        //Now compute the rest of the closure
        Queue<Rule> dq = new ArrayDeque<>(Arrays.asList(new Rule("__DUMMY__", next())));
        
        while(dq.size() > 0){
            Rule rule = dq.remove();
            String sym = rule.getRhs().firstTkn();
            
            if(!grammar.isNonTerminal(sym)) continue;
            
            for(Rule r : grammar.getRules(sym))
                if(res.add(new Item(grammar, r, 0)))
                    dq.add(r);
        }
        return res;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(rule.getLhs());
        sb.append(" :=");
        for(int i = 0; i < rule.getRhs().size(); i++){
            if(i == pos) sb.append(" . ");
            else sb.append(" ");
            sb.append(rule.getRhs().get(i));
        }
        if(isFinished()) sb.append(" .");
        return sb.toString();
        //●
    }
}