package compiler.items;
import java.util.*;

import compiler.Printable;
import compiler.sets.ComparableTreeSet;

public class ItemSet extends ComparableTreeSet<Item> implements Printable{

    public ItemSet(){}
    
    public ItemSet(List<Item> items){super(items);}
    
    public ItemSet(SortedSet<Item> items){super(items);}

    @Override
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