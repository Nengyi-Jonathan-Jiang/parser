package util.comparableSet;

import java.util.Collection;
import java.util.HashSet;

public class ComparableHashSet<T extends Comparable<T>> extends HashSet<T> implements ComparableSet<T> {
    public ComparableHashSet(){}
    public ComparableHashSet(Collection<? extends T> items){super(items);}

    public int compareTo(ComparableSet<T> other){
        return ComparableSet.compare(this, other);
    }
}