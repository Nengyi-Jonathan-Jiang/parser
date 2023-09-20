package frontend.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HashCompareCache<T extends Comparable<T>, U> implements Cache<T, U> {
    private final Map<Integer, Map<T, U>> cache = new HashMap<>();

    public void cache(T key, U value){
        var hash = key.hashCode();
        var m = cache.computeIfAbsent(hash, $ -> new TreeMap<>());
        m.put(key, value);
    }

    public U get(T key){
        var m = cache.get(key.hashCode());
        if(m == null) return null;
        return m.get(key);
    }
}
