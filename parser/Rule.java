package parser;
import java.util.*;

public class Rule implements Comparable<Rule>, Printable{
    private final String lhs;
    private final TokenString rhs;
    private final boolean empty;
    public Rule(String lhs, String... rhs){this(lhs, Arrays.asList(rhs));}
    public Rule(String lhs, List<String> rhs){this(lhs, new TokenString(rhs));}
    public Rule(String lhs, TokenString rhs){
        this.lhs = lhs;
        this.rhs = rhs;
        this.empty = rhs.size() == 0;
    }
    
    public String getLhs(){return lhs;}
    public TokenString getRhs(){return rhs;}
    public int getRhsSize(){return rhs.size();}
    public boolean isEmpty(){return empty;}
    
    public String toString(){
        return lhs + " := " + rhs;
    }
    
    public boolean equals(Rule that){
        return toString().equals(that.toString());
    }
    public int compareTo(Rule that){
        return toString().compareTo(that.toString());
    }
}
