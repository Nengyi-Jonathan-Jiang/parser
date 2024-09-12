package util.comparableSet;

import java.util.Collection;
import java.util.TreeSet;

public class ComparableTreeSet<T extends Comparable<T>> extends TreeSet<T> implements ComparableSet<T> {
    public ComparableTreeSet() {
    }

    public ComparableTreeSet(Collection<? extends T> items) {
        super(items);
    }

    public int compareTo(ComparableSet<T> other) {
        return ComparableSet.compare(this, other);
    }
}