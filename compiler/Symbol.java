package compiler;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Symbol implements Comparable<Symbol> {
    public static class SymbolTable {
        private final Map<String, Symbol> store = new TreeMap<>();
        private int size = 0;
        private boolean locked = false;

        public final Symbol
                __START__ = create("§"),
                __END__ = create("Ω"),
                __DUMMY__ = create("█");

        public Symbol create(String string){
            if(locked) throw new Error("Cannot create new symbol after table is locked");
            return store.containsKey(string) ? get(string) : new Symbol(this, string, ++size);
        }

        public Symbol get(String string) {
            if(!store.containsKey(string)) throw new Error("Symbol \"" + string + "\" does not exist");
            return store.get(string);
        }
        
        public int size() {
            if(!locked) throw new Error("Cannot query table size before table is locked");
            return size;
        }

        public List<Symbol> all() {
            return store.values().stream().toList();
        }

        public void lock() {
            locked = true;
        }

        public String toString() {
            return all().toString();
        }

        public static SymbolTable merge(SymbolTable a, SymbolTable b){
            SymbolTable res = new SymbolTable();
            for(var s : a.all()) res.create(s.string);
            for(var s : b.all()) res.create(s.string);
            return res;
        }
    }
    
    public final String string;
    private final SymbolTable table;
    private final int id;
    
    private Symbol(SymbolTable table, String string, int id) {
        this.table = table;
        this.id = id;
        this.string = string;
        table.store.put(string, this);
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int id() {
        return id;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public int compareTo(Symbol o) {
        return id - o.id;
    }

    public SymbolTable getTable() {
        return table;
    }
}
