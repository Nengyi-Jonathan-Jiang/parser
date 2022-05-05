package compiler.parsers;

import java.util.*;

import compiler.*;
import compiler.parsingTable.*;

/**
 * An abstract parser implementing the LR parsing algorithm. Subclasses
 * are expected to implement the {@code generateParseTable} method, which
 * should return a valid {@link ParsingTable} that the parser will use
 */
public abstract class Parser {
    protected ParsingTable table;

    /**
     * Makes a parser given a {@link Grammar}
     * @param grammar
     */
    public Parser(Grammar grammar){
        generateParsingTable(grammar);
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

    /**
     * @param grammar
     * @return 
     */
    protected abstract ParsingTable generateParsingTable(Grammar grammar);
}
