package compiler.parser;

import java.util.*;
import java.util.stream.Stream;

import compiler.Symbol;
import compiler.Token;

public class ParseTreeNode implements Iterable<ParseTreeNode> {
    private final Symbol description;
    private final ParseTreeNode[] children;
    private final Token value;

    public ParseTreeNode(Symbol description, Token value) {
        this.description = description;
        children = null;
        this.value = value;
    }

    public ParseTreeNode(Symbol description, ParseTreeNode... children) {
        this.description = description;
        this.children = children;
        this.value = null;
    }

    public Stream<ParseTreeNode> children() {
        assert children != null;
        return Arrays.stream(children);
    }
    public ParseTreeNode[] getChildren() {
        if (isLeaf()) throw new Error("Cannot access children of leaf node");
        return children;
    }
    public ParseTreeNode getChild(int i) {
        assert children != null;
        return children[i];
    }

    public Token getValue() {
        if (!isLeaf()) throw new Error("Cannot access value of non-leaf node");
        return value;
    }

    public Iterator<ParseTreeNode> iterator() {
        return children == null ? Collections.emptyIterator() : Arrays.asList(children).iterator();
    }

    public boolean isLeaf() {
        return children == null;
    }

    public Symbol getDescription() {
        return description;
    }

    public String toString() {
        if (children == null) return String.valueOf(value);
        if (children.length == 0) return description + " []";
        return description + " [" + indentEachLine(children().map(ParseTreeNode::toString).reduce("", (a, b) -> a + "\n" + b)) + "\n]";
    }

    private static String indentEachLine(String str) {
        return str.replace("\n", "\n    ");
    }

    public ParseTreeNode deepCopy() {
        if (isLeaf()) return new ParseTreeNode(description, value);
        return new ParseTreeNode(description, children().map(ParseTreeNode::deepCopy).toArray(ParseTreeNode[]::new));
    }
}