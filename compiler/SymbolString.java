package compiler;
import java.util.*;

public class SymbolString implements Iterable<Symbol>, Comparable<SymbolString>, Printable {
    private final Symbol[] tkns;
    private final int length;
    private final String repr;
    private final int hashCode;

    public SymbolString(Symbol... tokens) {
        this.tkns = tokens;
        length = tokens.length;

        StringBuilder s = new StringBuilder();
        if(tokens.length > 0) s.append(tokens[0]);
        for(int i = 1; i < tokens.length; i++) s.append(" ").append(tokens[i]);

        repr = s.toString();
        hashCode = repr.hashCode();
    }

    private SymbolString(String repr, Symbol... tokens) {
        this.tkns = tokens;
        length = tokens.length;

        this.repr = repr;
        hashCode = repr.hashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public Symbol get(int i){return tkns[i];}

    public Symbol firstTkn(){return length == 0 ? null : tkns[0];}

    public Symbol lastTkn(){return length == 0 ? null : tkns[length - 1];}
    
    public int size(){return length;}

    @Override
    public String toString(){return repr;}
    
    public boolean equals(Object that){
        return that instanceof SymbolString && repr.equals(that.toString());
    }
    
    public int compareTo(SymbolString that){
        return repr.compareTo(that.repr);
    }
    
    public List<Symbol> getList(){
        return List.of(tkns);
    }

    public SymbolString substr(int start){return substr(start, length);}
    public SymbolString substr(int start, int end){
        Symbol[] res = new Symbol[end - start];
        System.arraycopy(tkns, start, res, 0, end - start);
        return new SymbolString(res);
    }
    
    public Iterator<Symbol> iterator(){
        return List.of(tkns).iterator();
    }

    public SymbolString append (Symbol symbol){
        Symbol[] res = Arrays.copyOf(tkns, length + 1);
        res[length] = symbol;
        return new SymbolString(length == 0 ? symbol.toString() : repr + " " + symbol.toString(), res);
    }
}