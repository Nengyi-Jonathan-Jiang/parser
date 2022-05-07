package compiler.parsers;

import java.util.*;
import java.util.Map.Entry;

import compiler.*;
import compiler.grammar.Grammar;
import compiler.parsingTable.*;
import compiler.items.*;

/**
 * An abstract parser implementing the LR parsing algorithm. Subclasses
 * are expected to implement the {@code generateParseTable} method, which
 * should return a valid {@link ParsingTable} that the parser will use
 */
public abstract class LRParser implements Parser{
    protected ParsingTable table;
    protected Grammar grammar;

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

        // Start in state 0
        stateStack.push(0);

        int index = 0;
        while(index < tokens.length){
            int state = stateStack.peek();
            String token = tokens[index];

            TableEntry entry = table.getAction(state, token);

            // Parse failed
            if(entry == null){
                if(debug) System.out.println("Could not parse string!");
                return null;
            }
            
            
            if(debug) System.out.print(String.format("%-20s", parseTreeStack.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ")));

            switch(entry.getAction()){
                case SHIFT:
                    if(debug) System.out.println("SHIFT \"" + token + "\"");

                    // Update state stack and current token pointer
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

                    // Update state stack
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

    public void generateParsingTable(){
        Map<ItemSet, Integer> configuratingSets = generateConfiguratingSets();

        table = new ParsingTable(configuratingSets.size());

        for(Entry<ItemSet, Integer> entry : configuratingSets.entrySet()){
            ItemSet itemSet = entry.getKey();
            int state = entry.getValue();

            // Generate Action table
            for(Item item : itemSet){
                generateActionSetEntry(configuratingSets, state, itemSet, item);
            }
            
            // Generate Goto table
            for(String symbol : grammar.getNonTerminals()){
                Integer nextState = configuratingSets.get(successor(itemSet, symbol));
                if(nextState != null) table.setGoto(state, symbol, nextState);
            }
        }
    }

    public Map<ItemSet, Integer> generateConfiguratingSets(){
        Map<ItemSet, Integer> configuratingSets = new TreeMap<>();
        ItemSet initialState = closure(new Item(grammar, grammar.getStartRule(), 0, new ComparableSet<>("__END__")));
        configuratingSets.put(initialState, 0);
        
        Set<ItemSet> edge = new TreeSet<>(Arrays.asList(initialState));

        boolean updated = true;
        while(updated){
            updated = false;

            Map<ItemSet,Integer> newSet = new TreeMap<>();
            
            for(ItemSet configuratingSet : edge){
                for(String symbol : grammar.getAllSymbols()){
                    ItemSet successor = successor(configuratingSet, symbol);
                    if(!successor.isEmpty() && !newSet.containsKey(successor) && !configuratingSets.containsKey(successor)){
                        updated = true;
                        newSet.put(successor, configuratingSets.size() + newSet.size());
                        System.out.println("Found " + (configuratingSets.size() + newSet.size()) + "th configurating set (" + successor.size() + " items)");
                    }
                }
            }

            edge = newSet.keySet();
            configuratingSets.putAll(newSet);
        }

        return configuratingSets;
    }

    public void generateActionSetEntry(Map<ItemSet, Integer> configuratingSets, int state, ItemSet itemSet, Item item){
        if(item.isFinished() && item.getRule().equals(grammar.getStartRule())){
            table.setActionAccept(state, "__END__");
        }
        else if(item.isFinished()){
            Rule reduce = item.getRule();
            for(String symbol : item.getLookahead()){
                table.setActionReduce(state, symbol, reduce);
            }
        }
        else{
            Integer st2 = configuratingSets.get(successor(itemSet, item.next()));
            if(st2 != null) table.setActionShift(state, item.next(), st2);
        }
    }

    public abstract ItemSet closure(Item item);

    public ItemSet applyClosure(ItemSet itemSet){
        Set<Item> addedElements = new TreeSet<>();

        for(Item item : itemSet) 
            addedElements.addAll(closure(item));
        
        itemSet.addAll(addedElements);

        return itemSet;
    }
    
    public ItemSet closure(ItemSet itemSet){
        return applyClosure(itemSet.copy());
    }

    public ItemSet successor(ItemSet itemSet, String symbol){
        ItemSet res = new ItemSet();
        for(Item item : itemSet)
            if(!item.isFinished() && item.next().equals(symbol))
                res.add(item.shift());
        return applyClosure(res);
    }
}