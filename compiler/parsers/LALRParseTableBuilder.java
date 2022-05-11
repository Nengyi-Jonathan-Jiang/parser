package compiler.parsers;

import compiler.grammar.Grammar;
import compiler.items.Item;
import compiler.items.ItemSet;
import compiler.sets.ComparableHashSet;
import compiler.sets.ComparableTreeSet;

import java.util.*;

public class LALRParseTableBuilder extends LR1ParseTableBuilder{
    public LALRParseTableBuilder(Grammar grammar) {
        super(grammar);
    }

    protected Map<ItemSet, Integer> generateConfiguratingSets(){
        System.out.println("Generating configurating sets...");
        Map<ItemSet, Integer> configuratingSets = new TreeMap<>();
        successors = new TreeMap<>();
        ItemSet initialState = closure(new Item(grammar.getStartRule(), 0, new ComparableHashSet<>("__END__")));
        configuratingSets.put(initialState, 0);
        successors.put(0, new TreeMap<>());

        Set<ItemSet> edge = new TreeSet<>(Collections.singletonList(initialState));

        boolean updated = true;
        while(updated){
            updated = false;

            Set<ItemSet> newEdge = new TreeSet<>();

            for(ItemSet configuratingSet : edge){
                int state1 = configuratingSets.get(configuratingSet);
                for(String symbol : grammar.getAllSymbols()){
                    ItemSet successor = successor(configuratingSet, symbol);
                    if(successor.isEmpty()) continue;

                    int state2;

                    if(!configuratingSets.containsKey(successor)){
                        state2 = configuratingSets.size();
                        successors.put(state2, new TreeMap<>());
                        updated = true;
                        configuratingSets.put(successor, state2);
                        newEdge.add(successor);
                        System.out.println("Found " + (state2 + 1) + "th configurating set (" + successor.size() + " items)");
                    }
                    else state2 = configuratingSets.get(successor);

                    successors.get(state1).put(symbol, state2);
                }
            }

            edge = newEdge;
        }

        return configuratingSets;
    }


}

class ItemCore implements Comparable<ItemCore>{
    private final Item item;

    public ItemCore(Item item){
        this.item = item;
    }

    public int compareTo(ItemCore other){
        return item.coreCompareTo(other.getItem());
    }

    public Item getItem(){
        return item;
    }
}

class ItemCoreSet extends ComparableTreeSet<ItemCore> {
    public ItemCoreSet(Collection<ItemCore> items){
        super(items);
    }
    public ItemCoreSet(){
        super();
    }
}