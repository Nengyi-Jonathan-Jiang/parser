package compiler.parsers;

import java.util.*;

import compiler.Token;

public class ParseTree implements Iterable<ParseTree>{
    private final String description;
    private final ParseTree[] children;
    private final Token value;
    public ParseTree(String description, Token value){
        this.description = description;
        children = null;
        this.value = value;
    }
    public ParseTree(String description, ParseTree... children){
        this.description = description;
        this.children = children;
        this.value = null;
    }
    public ParseTree[] getChildren(){
        if(isLeaf()) throw new Error("Cannot access value of leaf node");
        return children;
    }
    public Token getValue(){
        if(!isLeaf()) throw new Error("Cannot access value of non-leaf node");
        return value;
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

    private static String indentEachLine(String str){
        return str.replace("\n", "\n    ");
    }

    public String prnt(){
        return 
            isLeaf() ? value.toString() : 
            children.length == 0 ? description + "{}" : 
            description + " {" +
                indentEachLine(Arrays.stream(children).map(ParseTree::prnt).reduce("",(a,b)-> a + "\n" + b))
            + "\n}";
    }
}