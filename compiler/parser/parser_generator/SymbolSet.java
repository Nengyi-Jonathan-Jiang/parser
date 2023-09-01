package compiler.parser.parser_generator;

import compiler.Symbol;
import compiler.util.ComparableHashSet;

import java.util.Collection;
import java.util.Objects;

public class SymbolSet extends ComparableHashSet<Symbol> {
    public SymbolSet() {}
    public SymbolSet(Collection<Symbol> other){
        super(other);
    }

    private boolean dirty = true;
    private String str;

    @Override
    public boolean addAll(Collection<? extends Symbol> c) {
        boolean res = super.addAll(c);
        dirty |= res;
        return res;
    }

    @Override
    public String toString() {
        if(dirty){
            str = generateString();
            dirty = false;
        }
        return str;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SymbolSet && Objects.equals(toString(), o.toString());
    }

    private String generateString(){
        StringBuilder s = new StringBuilder();
        var it = iterator();
        if (it.hasNext()) s.append(Integer.toString(it.next().id(), 36));
        while (it.hasNext()) s.append(" ").append(Integer.toString(it.next().id(), 36));
        return s.toString();
    }
}
