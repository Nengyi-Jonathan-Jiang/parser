package frontend.parser;

import frontend.Symbol;
import util.SerializableToString;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SymbolString implements Iterable<Symbol>, Comparable<SymbolString>, SerializableToString {
    private final Symbol[] symbols;
    private final int length;
    private final String _representation;

    public SymbolString(Symbol... tokens) {
        this.symbols = tokens;
        length = tokens.length;

        StringBuilder s = new StringBuilder();
        if (tokens.length > 0) s.append(tokens[0]);
        for (int i = 1; i < tokens.length; i++) s.append(" ").append(tokens[i]);

        _representation = s.toString();
    }

    private SymbolString(String _representation, Symbol... tokens) {
        this.symbols = tokens;
        length = tokens.length;

        this._representation = _representation;
    }

    public Symbol get(int i) {
        return symbols[i];
    }

    public Symbol firstTkn() {
        return length == 0 ? null : symbols[0];
    }

    public Symbol lastTkn() {
        return length == 0 ? null : symbols[length - 1];
    }

    public int size() {
        return length;
    }

    @Override
    public String toString() {
        return serializeToString();
    }

    @Override
    public void serializeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(_representation);
    }

    public boolean equals(Object that) {
        return that instanceof SymbolString && _representation.equals(that.toString());
    }

    public int compareTo(SymbolString that) {
        return _representation.compareTo(that._representation);
    }

    public List<Symbol> getList() {
        return List.of(symbols);
    }

    public SymbolString substring(int start) {
        return substring(start, length);
    }

    public SymbolString substring(int start, int end) {
        Symbol[] res = new Symbol[end - start];
        System.arraycopy(symbols, start, res, 0, end - start);
        return new SymbolString(res);
    }

    public Iterator<Symbol> iterator() {
        return List.of(symbols).iterator();
    }

    public SymbolString concat(Symbol symbol) {
        Symbol[] res = Arrays.copyOf(symbols, length + 1);
        res[length] = symbol;
        return new SymbolString(length == 0 ? symbol.toString() : _representation + " " + symbol.toString(), res);
    }
}