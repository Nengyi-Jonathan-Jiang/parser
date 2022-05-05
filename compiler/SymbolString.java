package compiler;
import java.util.*;

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
    public String firstTkn(){
        if(length == 0) return null;
        return tkns.get(0);
    }
    public String lastTkn(){
        if(length == 0) return null;
        return tkns.get(length - 1);
    }
    
    public int size(){return length;}
    
    public String toString(){return repr;}
    
    public boolean equals(SymbolString that){return repr.equals(that.repr);}
    
    public int compareTo(SymbolString that){return repr.compareTo(that.repr);}
    
    public List<String> getList(){return tkns.subList(0, length);}
    
    public SymbolString substr(int start){
        return substr(start, length);
    }
    public SymbolString substr(int start, int end){
        return new SymbolString(tkns.subList(start, end));
    }
    
    public Iterator<String> iterator(){
        return tkns.iterator();
    }
}
