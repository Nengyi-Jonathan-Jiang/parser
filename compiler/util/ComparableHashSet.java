package compiler.util;
import java.util.*;

public class ComparableHashSet<T extends Comparable<T>> extends HashSet<T> implements ComparableSet<T>{
    public ComparableHashSet(){}
    public ComparableHashSet(Collection<? extends T> items){super(items);}
    @SafeVarargs
    public ComparableHashSet(T... items){super(Arrays.asList(items));}

    public int compareTo(ComparableSet<T> other){
        return ComparableSet.compare(this, other);
    }
}