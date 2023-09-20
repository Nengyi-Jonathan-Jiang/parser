package frontend.parser;
import frontend.Symbol;
import frontend.util.Printable;

import java.util.*;

public class SymbolString implements Iterable<Symbol>, Comparable<SymbolString>, Printable {
    private final Symbol[] symbols;
    private final int length;
    private final String _repr;

    public SymbolString(Symbol... tokens) {
        this.symbols = tokens;
        length = tokens.length;

        StringBuilder s = new StringBuilder();
        if(tokens.length > 0) s.append(tokens[0]);
        for(int i = 1; i < tokens.length; i++) s.append(" ").append(tokens[i]);

        _repr = s.toString();
    }

    private SymbolString(String _repr, Symbol... tokens) {
        this.symbols = tokens;
        length = tokens.length;

        this._repr = _repr;
    }

    public Symbol get(int i){return symbols[i];}

    public Symbol firstTkn(){return length == 0 ? null : symbols[0];}

    public Symbol lastTkn(){return length == 0 ? null : symbols[length - 1];}
    
    public int size(){return length;}

    @Override
    public String toString(){return _repr;}
    
    public boolean equals(Object that){
        return that instanceof SymbolString && _repr.equals(that.toString());
    }
    
    public int compareTo(SymbolString that){
        return _repr.compareTo(that._repr);
    }
    
    public List<Symbol> getList(){
        return List.of(symbols);
    }

    public SymbolString substring(int start){return substring(start, length);}
    public SymbolString substring(int start, int end){
        Symbol[] res = new Symbol[end - start];
        System.arraycopy(symbols, start, res, 0, end - start);
        return new SymbolString(res);
    }
    
    public Iterator<Symbol> iterator(){
        return List.of(symbols).iterator();
    }

    public SymbolString concat (Symbol symbol){
        Symbol[] res = Arrays.copyOf(symbols, length + 1);
        res[length] = symbol;
        return new SymbolString(length == 0 ? symbol.toString() : _repr + " " + symbol.toString(), res);
    }
}