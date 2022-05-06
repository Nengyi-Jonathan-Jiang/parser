package compiler;
import java.util.*;

public class ComparableSet<T extends Comparable<T>> extends TreeSet<T> implements Comparable<ComparableSet<T>>{
    public ComparableSet(){}
    public ComparableSet(Collection<? extends T> items){super(items);}
    public ComparableSet(SortedSet<? extends T> items){super(items);}
    @SafeVarargs
    public ComparableSet(T... items){super(Arrays.asList(items));}

    // public
    public int compareTo(ComparableSet<T> other){
        Iterator<T> i1 = iterator();
        Iterator<T> i2 = other.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            int res = i1.next().compareTo(i2.next());
            if (res != 0) return res;
        }
        return i1.hasNext() ? 1 : i2.hasNext() ? -1 : 0;
    }
}