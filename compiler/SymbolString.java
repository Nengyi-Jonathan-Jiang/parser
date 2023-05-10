package compiler;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.*;

public class SymbolString implements Iterable<Symbol>, Comparable<SymbolString>, Printable {
    private final Symbol[] tkns;
    private final int length;
    private final String repr;
    private final BigInteger hash;

    public SymbolString(Symbol... tokens) {
        this.tkns = tokens;
        length = tokens.length;
        repr = Arrays.stream(tokens).map(Symbol::toString).collect(Collectors.joining(" "));

        // Generate hash for easy compare
        BigInteger h = BigInteger.ZERO;
        for (Symbol token : tokens) h = h.add(BigInteger.valueOf(token.id()));
        hash = h;
    }
    
    public Symbol get(int i){return tkns[i];}

    public Symbol firstTkn(){return length == 0 ? null : tkns[0];}

    public Symbol lastTkn(){return length == 0 ? null : tkns[length - 1];}
    
    public int size(){return length;}

    @Override
    public String toString(){return repr;}
    
    public boolean equals(SymbolString that){
        return hash.equals(that.hash);
    }
    
    public int compareTo(SymbolString that){return repr.compareTo(that.repr);}
    
    public List<Symbol> getList(){
        return List.of(tkns);
    }

    public Stream<Symbol> stream(){
        return Arrays.stream(tkns);
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
        Symbol[] res = new Symbol[length + 1];
        System.arraycopy(tkns, 0, res, 0, length);
        res[length] = symbol;
        return new SymbolString(res);
    }
}
