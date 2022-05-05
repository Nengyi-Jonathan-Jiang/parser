package parser.grammar;
import java.util.*;

import parser.ComparableSet;
import parser.Printable;

public class ItemSet extends ComparableSet<Item> implements Printable{

    public ItemSet(){}
    
    public ItemSet(List<Item> items){super(items);}
    
    public ItemSet(TreeSet<Item> items){super(items);}
    
    public ItemSet(ItemSet set){super(set);}
    
    public boolean add(Collection<Item> items) {return super.addAll(items);}
    
    public ItemSet mergeWith(ItemSet set) {add(set); return this;}
    
    public ItemSet merge(ItemSet set){
        return new ItemSet(this).mergeWith(set);
    }
    
    public ItemSet applyClosure(){
        Deque<Item> addedElements = new ArrayDeque<>();
        for(Item item : this)
            for(Item newItem : item.closure())
                addedElements.push(newItem);
        
        addAll(addedElements);
        return this;
    }
    
    public ItemSet closure(){
        return new ItemSet(this).applyClosure();
    }
    
    public ItemSet successor(String sym){
        ItemSet res = new ItemSet();
        for(Item it : this)
            if(sym.equals(it.next()))
                res.add(it.shift());
        return res.applyClosure();
    }
    
    public int compareTo(ItemSet other){
        Iterator<Item> i1 = iterator();
        Iterator<Item> i2 = other.iterator();
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
}