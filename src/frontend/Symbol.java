package frontend;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Symbol implements Comparable<Symbol> {
    public static class SymbolTable {
        private final Map<String, Symbol> store = new TreeMap<>();
        private int size = 0;
        private boolean locked = false;

        public final Symbol __START__ = create("§"), __END__ = create("Ω");

        private Symbol createNewSymbol(String string) {
            if(locked) throw new Error("Cannot create new symbol after table is locked");
            return new Symbol(this, string, ++size);
        }

        public Symbol create(String string){
            return store.containsKey(string) ? get(string) : createNewSymbol(string);
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
            for(var s : a.all()) res.create(s.name);
            for(var s : b.all()) res.create(s.name);
            return res;
        }
    }
    
    public final String name;
    private final SymbolTable table;
    private final int id;
    
    private Symbol(SymbolTable table, String name, int id) {
        this.table = table;
        this.id = id;
        this.name = name;
        table.store.put(name, this);
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
        return this.name;
    }

    @Override
    public int compareTo(Symbol o) {
        return id - o.id;
    }

    public SymbolTable getTable() {
        return table;
    }
}