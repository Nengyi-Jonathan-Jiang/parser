package compiler.parsers;

import java.util.*;

import compiler.Token;

public class ParseTree implements Iterable<ParseTree>{
    private final Symbol description;
    private final ParseTree[] children;
    private final Token value;
    public ParseTree(Symbol description, Token value){
        this.description = description;
        children = null;
        this.value = value;
    }
    public ParseTree(Symbol description, ParseTree... children){
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
    public Symbol getDescription(){
        return description;
    }
    public Symbol toString(){
        return description;
    }

    private static Symbol indentEachLine(Symbol str){
        return str.replace("\n", "\n    ");
    }

    public Symbol prnt(){
        return 
            isLeaf() ? value.toString() : 
            children.length == 0 ? description + "{}" : 
            description + " {" +
                indentEachLine(Arrays.stream(children).map(ParseTree::prnt).reduce("",(a,b)-> a + "\n" + b))
            + "\n}";
    }
}