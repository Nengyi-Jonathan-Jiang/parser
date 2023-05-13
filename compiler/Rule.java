package compiler;
import java.util.*;

public class Rule implements Comparable<Rule>, Printable{
    private static int _id = 0;
    private final int id = ++_id;

    private final Symbol lhs;
    private final SymbolString rhs;
    private final boolean empty;

    public Rule(Symbol lhs, Symbol... rhs){this(lhs, new SymbolString(rhs));}
    public Rule(Symbol lhs, List<Symbol> rhs){this(lhs, rhs.toArray(Symbol[]::new));}
    public Rule(Symbol lhs, SymbolString rhs){
        this.lhs = lhs;
        this.rhs = rhs;
        this.empty = rhs.size() == 0;
    }

    public Symbol getLhs(){return lhs;}
    public SymbolString getRhs(){return rhs;}
    public int getRhsSize(){return rhs.size();}
    public boolean isEmpty(){return empty;}

    @Override
    public String toString(){
        return lhs + " := " + rhs;
    }

    @Override
    public int compareTo(Rule that){
//        return hashCode() - that.hashCode();
        return id - that.id;
    }
}