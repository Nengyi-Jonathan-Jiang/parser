package parser;
import java.util.*;

public class TokenString implements Iterable<String>, Comparable<TokenString>, Printable{
    private final List<String> tkns;
    private final int length;
    private final String repr;
    public TokenString(String... tokens){this(Arrays.asList(tokens));}
    public TokenString(List<String> tokens){
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
    
    public boolean equals(TokenString that){return repr.equals(that.repr);}
    
    public int compareTo(TokenString that){return repr.compareTo(that.repr);}
    
    public List<String> getList(){return tkns.subList(0, length);}
    
    public TokenString substr(int start){
        return substr(start, length);
    }
    public TokenString substr(int start, int end){
        return new TokenString(tkns.subList(start, end));
    }
    
    public Iterator<String> iterator(){
        return tkns.iterator();
    }
}
