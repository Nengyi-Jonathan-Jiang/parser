package compiler;
import java.util.*;
import java.util.stream.*;

public class SymbolString implements Iterable<String>, Comparable<SymbolString>, Printable{
    private final List<String> tkns;
    private final int length;
    private final String repr;
    public SymbolString(String... tokens){this(Arrays.asList(tokens));}
    public SymbolString(List<String> tokens){
        this.tkns = tokens;
        length = tokens.size();
        repr = String.join(" ", tokens);
    }
    
    public String get(int i){return tkns.get(i);}

    public String firstTkn(){return length == 0 ? null : tkns.get(0);}

    public String lastTkn(){return length == 0 ? null : tkns.get(length - 1);}
    
    public int size(){return length;}
    
    public String toString(){return repr;}
    
    public boolean equals(SymbolString that){return repr.equals(that.repr);}
    
    public int compareTo(SymbolString that){return repr.compareTo(that.repr);}
    
    public List<String> getList(){return tkns.subList(0, length);}

    public Stream<String> stream(){return tkns.stream();}
    
    public SymbolString substr(int start){return substr(start, length);}
    public SymbolString substr(int start, int end){
        return new SymbolString(tkns.subList(start, end));
    }
    
    public Iterator<String> iterator(){return tkns.iterator();}

    public SymbolString concat(SymbolString other){
        return new SymbolString(Stream.concat(stream(), other.stream()).collect(Collectors.toList()));
    }
    public SymbolString concat(String symbol){
        return new SymbolString(Stream.concat(stream(), Stream.of(symbol)).collect(Collectors.toList()));
    }
}
