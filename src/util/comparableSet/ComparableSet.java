package util.comparableSet;

import java.util.Iterator;
import java.util.Set;

public interface ComparableSet<T extends Comparable<T>> extends Comparable<ComparableSet<T>>, Set<T>{
    
    static <T extends Comparable<T>> int compare(ComparableSet<T> a, ComparableSet<T> b){
        Iterator<T> i1 = a.iterator();
        Iterator<T> i2 = b.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            int res = i1.next().compareTo(i2.next());
            if (res != 0) return res;
        }
        return i1.hasNext() ? 1 : i2.hasNext() ? -1 : 0;
    }
}