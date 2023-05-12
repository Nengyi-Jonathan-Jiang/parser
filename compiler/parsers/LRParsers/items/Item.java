package compiler.parsers.LRParsers.items;

import compiler.Printable;
import compiler.Rule;
import compiler.Symbol;
import compiler.sets.ComparableSet;

import java.math.BigInteger;
import java.util.Collection;

public class Item implements Comparable<Item>, Printable{
    private final Rule rule;
    private final int pos;
    private final ComparableSet<Symbol> lookahead;
    private final String lookaheadRepr;

    public Item(Rule rule, int pos, ComparableSet<Symbol> lookahead){
        this.rule = rule;
        this.pos = pos;
        this.lookahead = lookahead;
        this.lookaheadRepr = createRepr(lookahead);
    }

    public static String createRepr(Collection<Symbol> symbols){
        StringBuilder s = new StringBuilder();
        var it = symbols.iterator();
        if(it.hasNext()) s.append(it.next());
        while(it.hasNext()) s.append(" ").append(it.next());
        return s.toString();
    }
    
    public Rule getRule(){return rule;}
    public int getPos(){return pos;}
    
    public boolean isFinished(){
        return pos >= rule.getRhsSize();
    }
    
    public Symbol next(){
        if(isFinished()) return null;
        return rule.getRhs().get(pos);
    }
    
    public Item shift(){
        if(isFinished()) throw new IndexOutOfBoundsException();
        return new Item(rule, pos + 1, lookahead);
    }
    
    public int compareTo(Item other){
        int coreCompare = coreCompareTo(other);
        return coreCompare == 0 ? lookaheadCompareTo(other) : coreCompare;
    }

    public int lookaheadCompareTo(Item other) {
//        return lookahead.compareTo(other.getLookahead());
        return lookaheadRepr.compareTo(other.lookaheadRepr);
    }

    public int coreCompareTo(Item other){
        if(pos != other.getPos()) return pos - other.getPos();
        return rule.compareTo(other.getRule());
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

    public ComparableSet<Symbol> getLookahead(){
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