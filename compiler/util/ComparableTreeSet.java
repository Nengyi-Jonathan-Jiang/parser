package compiler.util;
import java.util.*;

public class ComparableTreeSet<T extends Comparable<T>> extends TreeSet<T> implements ComparableSet<T>{
    public ComparableTreeSet(){}
    public ComparableTreeSet(Collection<? extends T> items){super(items);}

    public int compareTo(ComparableSet<T> other){
        return ComparableSet.compare(this, other);
    }
}