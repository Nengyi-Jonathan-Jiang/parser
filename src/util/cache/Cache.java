package util.cache;

public interface Cache<T, U> {
    void cache(T key, U value);
    U get(T key);
}
