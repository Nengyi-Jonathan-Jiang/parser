package compiler.util;

public interface Cache<T, U> {
    void cache(T key, U value);
    U get(T key);
}
