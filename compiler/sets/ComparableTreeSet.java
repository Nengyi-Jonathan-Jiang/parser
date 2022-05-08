package compiler.sets;
import java.util.*;

public class ComparableTreeSet<T extends Comparable<T>> extends TreeSet<T> implements ComparableSet<T>{
    public ComparableTreeSet(){}
    public ComparableTreeSet(Collection<? extends T> items){super(items);}
    @SafeVarargs
    public ComparableTreeSet(T... items){super(Arrays.asList(items));}

    public int compareTo(ComparableSet<T> other){
        return ComparableSet.compare(this, other);
    }
}