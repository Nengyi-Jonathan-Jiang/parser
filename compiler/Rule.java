package compiler;
import java.util.*;

public class Rule implements Comparable<Rule>, Printable{
    private final String lhs;
    private final SymbolString rhs;
    private final boolean empty;

    public Rule(String lhs, String... rhs){this(lhs, Arrays.asList(rhs));}
    public Rule(String lhs, List<String> rhs){this(lhs, new SymbolString(rhs));}
    public Rule(String lhs, SymbolString rhs){
        this.lhs = lhs;
        this.rhs = rhs;
        this.empty = rhs.size() == 0;
    }
    
    public String getLhs(){return lhs;}
    public SymbolString getRhs(){return rhs;}
    public int getRhsSize(){return rhs.size();}
    public boolean isEmpty(){return empty;}

    @Override
    public String toString(){
        return lhs + " := " + rhs;
    }
    
    public boolean equals(Rule that){
        // return toString().equals(that.toString());
        return this == that;
    }

    @Override
    public int compareTo(Rule that){
        // return toString().compareTo(that.toString());
        return hashCode() - that.hashCode();
    }
}
