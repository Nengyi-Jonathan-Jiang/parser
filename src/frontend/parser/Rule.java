package frontend.parser;
import frontend.Symbol;
import frontend.util.Printable;

import java.util.*;

public class Rule implements Comparable<Rule>, Printable {
    private static int _id = 0;
    public final int id = ++_id;
    public final boolean unwrap;
    public final boolean chained;

    public final Symbol lhs;
    public final SymbolString rhs;
    public final boolean empty;

    public Rule(Symbol lhs, boolean unwrap, boolean chained, List<Symbol> rhs){
        this(lhs, chained, unwrap, rhs.toArray(Symbol[]::new));
    }
    public Rule(Symbol lhs, boolean chained, boolean unwrap, Symbol... rhs){
        this(lhs, new SymbolString(rhs), chained, unwrap);
    }
    public Rule(Symbol lhs, SymbolString rhs, boolean chained, boolean unwrap){
        this.lhs = lhs;
        this.rhs = rhs;
        this.empty = rhs.size() == 0;
        this.chained = chained;
        this.unwrap = unwrap;
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
        return id - that.id;
    }
}