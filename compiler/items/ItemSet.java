package compiler.items;
import java.util.*;

import compiler.ComparableSet;
import compiler.Printable;

public class ItemSet extends ComparableSet<Item> implements Printable{

    public ItemSet(){}
    
    public ItemSet(List<Item> items){super(items);}
    
    public ItemSet(TreeSet<Item> items){super(items);}
    
    public ItemSet(ItemSet set){super(set);}
    
    public int compareTo(ItemSet other){
        Iterator<Item> i1 = iterator(), i2 = other.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            int res = i1.next().compareTo(i2.next());
            if (res != 0) return res;
        }
        if (i1.hasNext()) return 1;
        if (i2.hasNext()) return -1;
        return 0;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(Item it : this){
            sb.append("\n\t");
            sb.append(it.toString());
        }
        sb.append("\n}");
        return sb.toString();
    }

    public ItemSet copy(){
        return new ItemSet(this);
    }
}