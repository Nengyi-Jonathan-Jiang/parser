package frontend.parser;

import frontend.Symbol;
import frontend.util.Printable;
import frontend.util.SerializableToString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Rule implements Comparable<Rule>, Printable, SerializableToString {
    private static int _id = 0;
    public final int id = ++_id;
    public final boolean unwrapMono;
    public final boolean chained;

    public final Symbol lhs;
    public final SymbolString rhs;
//    public final RuleOptions[] ruleOptions = {};

    public final boolean empty;

    public Rule(Symbol lhs, boolean unwrapMono, boolean chained, List<Symbol> rhs) {
        this(lhs, chained, unwrapMono, rhs.toArray(Symbol[]::new));
    }

    public Rule(Symbol lhs, boolean chained, boolean unwrapMono, Symbol... rhs) {
        this(lhs, new SymbolString(rhs), chained, unwrapMono);
    }

    public Rule(Symbol lhs, SymbolString rhs, boolean chained, boolean unwrapMono) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.empty = rhs.size() == 0;
        this.chained = chained;
        this.unwrapMono = unwrapMono;
    }

    public Symbol getLhs() {
        return lhs;
    }

    public SymbolString getRhs() {
        return rhs;
    }

    public int getRhsSize() {
        return rhs.size();
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public String toString() {
        return serializeToString();
    }

    @Override
    public void writeToStringBuilder(StringBuilder stringBuilder) {
        lhs.writeToStringBuilder(stringBuilder);
        stringBuilder.append(" := ");
        rhs.writeToStringBuilder(stringBuilder);
    }

    @Override
    public int compareTo(Rule that) {
        return id - that.id;
    }

    public ParseTreeNode applyTo(ParseTreeNode[] children) {
        // Unwrap node if allowed to simplify the parse tree
        if (children.length == 1 && unwrapMono) {
            return children[0];
        }

        if (chained) {
            ParseTreeNode reducer = children[0];
            if (reducer.matches(lhs)) {
                var joinedChildren = Stream.concat(
                    reducer.children(),
                    Arrays.stream(children).skip(1)
                ).toArray(ParseTreeNode[]::new);
                return new ParseTreeNode(lhs, joinedChildren);
            }
        }

        return new ParseTreeNode(lhs, children);
    }
}