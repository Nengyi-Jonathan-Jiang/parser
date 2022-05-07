package compiler.items;

import compiler.ComparableSet;
import compiler.Printable;
import compiler.Rule;

public class Item implements Comparable<Item>, Printable{
    private final Rule rule;
    private final int pos;
    private final ComparableSet<String> lookahead;

    public Item(Rule rule, int pos, ComparableSet<String> lookahead){
        this.rule = rule;
        this.pos = pos;
        this.lookahead = lookahead;

    }
    
    public Rule getRule(){return rule;}
    public int getPos(){return pos;}
    
    public boolean isFinished(){
        return pos >= rule.getRhsSize();
    }
    
    public String next(){
        if(isFinished()) return null;
        return rule.getRhs().get(pos);
    }
    
    public Item shift(){
        if(isFinished()) throw new IndexOutOfBoundsException();
        return new Item(rule, pos + 1, lookahead);
    }
    
    public int compareTo(Item other){
        if(pos != other.getPos()) return pos - other.getPos();
        int ruleCompare = rule.compareTo(other.getRule());
        if(ruleCompare != 0) return ruleCompare;
        return lookahead.compareTo(other.getLookahead());
    }
    
    public int hashCode(){
        final int prime = 1500450271;
        return prime * (prime * (prime * rule.hashCode() + pos) + lookahead.hashCode());
    }

    public boolean equals(Item other){
        return pos == other.pos && rule.equals(other.rule) && lookahead.equals(other.lookahead);
    }

    public boolean coreEquals(Item other){
        return pos == other.pos && rule.equals(other.rule);
    }

    public ComparableSet<String> getLookahead(){
        return lookahead;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(rule.getLhs());
        sb.append(" :=");
        for(int i = 0; i < rule.getRhsSize(); i++){
            if(i == pos) sb.append(" ● ");
            else sb.append(" ");
            sb.append(rule.getRhs().get(i));
        }
        if(isFinished()) sb.append(" ●");
        sb.append(" ");
        sb.append(lookahead.stream().map(i -> "\"" + i + "\"").reduce("", (a, b) -> a.equals("") ? "\t\t" + b : a + " / " + b));
        return sb.toString();
    }
}