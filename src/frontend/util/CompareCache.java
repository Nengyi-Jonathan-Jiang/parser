package frontend.util;

import java.util.TreeMap;

public class CompareCache<T extends Comparable<T>, U> implements Cache<T, U> {
    final TreeMap<T, U> m = new TreeMap<>();

    @Override
    public void cache(T key, U value) {
        m.put(key, value);
    }

    @Override
    public U get(T key) {
        return m.get(key);
    }
}