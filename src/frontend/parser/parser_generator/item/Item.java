package frontend.parser.parser_generator.item;

import frontend.Symbol;
import frontend.parser.Rule;
import frontend.parser.parser_generator.SymbolSet;
import util.comparableSet.ComparableSet;
import util.SerializableToString;

public class Item implements Comparable<Item>, SerializableToString {
    private final Rule rule;
    private final int pos;
    private final SymbolSet lookahead;
    private final String lookaheadRepr;


    public Item(Rule rule, int pos, SymbolSet lookahead) {
        this.rule = rule;
        this.pos = pos;
        this.lookahead = lookahead;

        lookaheadRepr = lookahead.toString();
    }

    public Rule getRule() {
        return rule;
    }

    public int getPos() {
        return pos;
    }

    public boolean isFinished() {
        return pos >= rule.getRhsSize();
    }

    public Symbol next() {
        if (isFinished()) return null;
        return rule.getRhs().get(pos);
    }

    public Item shift() {
        if (isFinished()) throw new IndexOutOfBoundsException();
        return new Item(rule, pos + 1, lookahead);
    }

    public int compareTo(Item other) {
        int coreCompare = coreCompareTo(other);
        return coreCompare == 0 ? lookaheadCompareTo(other) : coreCompare;
    }

    public int lookaheadCompareTo(Item other) {
        return lookaheadRepr.compareTo(other.lookaheadRepr);
    }

    public int coreCompareTo(Item other) {
        if (pos != other.getPos()) return pos - other.getPos();
        return rule.compareTo(other.getRule());
    }

    public int hashCode() {
        final int prime = 1500450271;
        return prime * (prime * (prime * rule.hashCode() + pos) + lookahead.hashCode());
    }

    public ComparableSet<Symbol> getLookahead() {
        return lookahead;
    }

    @Override
    public void serializeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(rule.getLhs());
        stringBuilder.append(" :=");
        for (int i = 0; i < rule.getRhsSize(); i++) {
            if (i == pos) stringBuilder.append(" ● ");
            else stringBuilder.append(" ");
            stringBuilder.append(rule.getRhs().get(i));
        }
        if (isFinished()) stringBuilder.append(" ●");
        stringBuilder.append(" ");
        stringBuilder.append(
            lookahead
                .stream()
                .map(i -> "\"" + i + "\"")
                .reduce("", (a, b) -> a.isEmpty() ? "\t\t" + b : a + " / " + b)
        );
    }

    public String toString() {
        return serializeToString();
    }
}