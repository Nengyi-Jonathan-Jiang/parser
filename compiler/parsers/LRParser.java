package compiler.parsers;

import java.util.*;
import java.util.Map.Entry;

import compiler.*;
import compiler.grammar.Grammar;
import compiler.parsingTable.*;
import compiler.items.*;

/**
 * An abstract table-driven parser implementing the LR parsing algorithm. Table
 * generation may be slow but the actual parsing runs in O(n) time
 */
public abstract class LRParser implements Parser{
    protected ParsingTable table;
    protected Grammar grammar;

    private Map<Integer, Map<String, Integer>> successors;

    /**
     * Makes a parser given a {@link Grammar}
     * @param grammar
     */
    public LRParser(Grammar grammar){
        this.grammar = grammar;
        generateParsingTable();
    }

    public ParseTree parse(String[] tokens){return parse(tokens, false);}
    /**
     * Parses a string of tokens
     * @param tokens A string of tokens to be parsed
     * @param debug Whether to print out the parsing steps
     * @return The parse tree if the tokens were parsed successfully, otherwise null
     */
    public ParseTree parse(String[] tokens, boolean debug){

        Deque<Integer> stateStack = new ArrayDeque<>();
        Deque<ParseTree> parseTreeStack = new ArrayDeque<>();

        // Start in state1 0
        stateStack.push(0);

        int index = 0;
        while(index < tokens.length){
            int state1 = stateStack.peek();
            String token = tokens[index];

            TableEntry entry = table.getAction(state1, token);

            // Parse failed
            if(entry == null){
                if(debug) System.out.println("Could not parse string!");
                return null;
            }
            
            
            if(debug) System.out.print(String.format("%-20s", parseTreeStack.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ")));

            switch(entry.getAction()){
                case SHIFT:
                    if(debug) System.out.println("SHIFT \"" + token + "\"");

                    // Update state1 stack and current token pointer
                    stateStack.push(((ShiftEntry)entry).getNextState());
                    index++;

                    // Update parse tree -- add new leaf node to stack
                    parseTreeStack.push(new ParseTree(token));
                    break;

                case ACCEPT: //Parse successful -- return parse tree
                    if(debug) System.out.println("ACCEPTED INPUT");
                    return parseTreeStack.getFirst();

                case REDUCE:
                    Rule reduceRule = ((ReduceEntry)entry).getRule();
                    String lhs = reduceRule.getLhs();

                    if(debug) System.out.println("REDUCE " + reduceRule);

                    // Update state1 stack
                    for(int j = 0; j < reduceRule.getRhsSize(); j++) stateStack.pop();
                    GotoEntry gotoEntry = (GotoEntry)table.getGoto(stateStack.peek(), lhs);
                    stateStack.push(gotoEntry.getNextState());

                    // Update parse tree - merge nodes into parent node

                    if(reduceRule.getRhs().size() == 1) break;  //Simplify parse tree - remove unnecessary wrapping
                    ParseTree[] children = new ParseTree[reduceRule.getRhsSize()];
                    for(int j = reduceRule.getRhsSize() - 1; j >= 0; j--)
                        children[j] = parseTreeStack.pop();
                    parseTreeStack.push(new ParseTree(lhs, children));
                    break;
                
                default:
            }
        }
        return null;
    }

    protected void generateParsingTable(){
        Map<ItemSet, Integer> configuratingSets = generateConfiguratingSets();

        System.out.println("Generating parsing table entries...");

        table = new ParsingTable(configuratingSets.size());

        int i = 0;
        for(Entry<ItemSet, Integer> entry : configuratingSets.entrySet()){
            ItemSet itemSet = entry.getKey();
            int state1 = entry.getValue();

            System.out.println("Generated entries for " + (++i) + " states (currently on state " + state1 + ")");

            // Generate Action table
            for(Item item : itemSet){
                generateActionSetEntry(configuratingSets, state1, itemSet, item);
            }
            
            // Generate Goto table
            for(String symbol : grammar.getNonTerminals()){
                // Integer nextState = configuratingSets.get(successor(itemSet, symbol));
                Integer nextState = successors.get(configuratingSets.get(itemSet)).get(symbol);
                if(nextState != null) table.setGoto(state1, symbol, nextState);
            }
        }
    }

    protected Map<ItemSet, Integer> generateConfiguratingSets(){
        System.out.println("Generating configurating sets...");
        Map<ItemSet, Integer> configuratingSets = new TreeMap<>();
        successors = new TreeMap<>();
        ItemSet initialState = closure(new Item(grammar.getStartRule(), 0, new ComparableSet<>("__END__")));
        configuratingSets.put(initialState, 0);
        successors.put(0, new TreeMap<>());
        
        Set<ItemSet> edge = new TreeSet<>(Arrays.asList(initialState));

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
                        System.out.println("Found " + state2 + 1 + "th configurating set (" + successor.size() + " items)");
                    }
                    else state2 = configuratingSets.get(successor);

                    successors.get(state1).put(symbol, state2);
                }
            }

            edge = newEdge;
        }

        return configuratingSets;
    }

    protected void generateActionSetEntry(Map<ItemSet, Integer> configuratingSets, int state1, ItemSet itemSet, Item item){
        if(item.isFinished() && item.getRule().equals(grammar.getStartRule())){
            table.setActionAccept(state1, "__END__");
        }
        else if(item.isFinished()){
            Rule reduce = item.getRule();
            for(String symbol : item.getLookahead()){
                table.setActionReduce(state1, symbol, reduce);
            }
        }
        else{
            // Integer st2 = configuratingSets.get(successor(itemSet, item.next()));
            Integer st2 = successors.get(configuratingSets.get(itemSet)).get(item.next());
            if(st2 != null) table.setActionShift(state1, item.next(), st2);
        }

        
    }

    /**
     * Computes the closure of an item. Will be implemented
     * differently depending on the table generation algorithm
     */
    protected abstract ItemSet closure(Item item);

    protected ItemSet closure(ItemSet itemSet){
        Set<Item> addedElements = new TreeSet<>();

        for(Item item : itemSet) 
            addedElements.addAll(closure(item));
        
        itemSet.addAll(addedElements);

        return itemSet;
    }

    protected ItemSet successor(ItemSet itemSet, String symbol){
        ItemSet res = new ItemSet();
        for(Item item : itemSet)
            if(!item.isFinished() && item.next().equals(symbol))
                res.add(item.shift());
        return closure(res);
    }
}