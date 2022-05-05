package parser.parsers;

import java.util.*;

public class ParseTree implements Iterable<ParseTree>{
    private final String description;
    private final ParseTree[] children;
    public ParseTree(String description){
        this.description = description;
        children = null;
    }
    public ParseTree(String description, ParseTree... children){
        this.description = description;
        this.children = children;
    }
    public Iterator<ParseTree> iterator(){
        return Arrays.asList(children).iterator();
    }
    public boolean isLeaf(){
        return children == null;
    }
    public String getDescription(){
        return description;
    }
    public String toString(){
        return description;
    }
}