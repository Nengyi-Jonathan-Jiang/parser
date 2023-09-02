package compiler.parser;

import java.util.*;

import compiler.Symbol;
import compiler.Token;

public class ParseTreeNode implements Iterable<ParseTreeNode>{
    private final Symbol description;
    private final ParseTreeNode[] children;
    private final Token value;
    public ParseTreeNode(Symbol description, Token value){
        this.description = description;
        children = null;
        this.value = value;
    }
    public ParseTreeNode(Symbol description, ParseTreeNode... children){
        this.description = description;
        this.children = children;
        this.value = null;
    }
    public ParseTreeNode[] getChildren(){
        if(isLeaf()) throw new Error("Cannot access children of leaf node");
        return children;
    }
    public Token getValue(){
        if(!isLeaf()) throw new Error("Cannot access value of non-leaf node");
        return value;
    }
    public Iterator<ParseTreeNode> iterator(){
        return children == null ? Collections.emptyIterator() : Arrays.asList(children).iterator();
    }
    public boolean isLeaf(){
        return children == null;
    }
    public Symbol getDescription(){
        return description;
    }
    public String toString(){
        return description.name;
    }

    private static String indentEachLine(String str){
        return str.replace("\n", "\n    ");
    }

    public String prnt(){
        if (children == null) return String.valueOf(value);
        if (children.length == 0) return description + " []";
        return description + " [" + indentEachLine(Arrays.stream(children).map(ParseTreeNode::prnt).reduce("", (a, b) -> a + "\n" + b)) + "\n]";
    }
}