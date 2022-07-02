package compiler.parsers.LRParsers;

import java.util.*;

import compiler.*;
import compiler.parsers.ParseTree;
import compiler.parsers.Parser;
import compiler.parsers.LRParsers.parsing_table.*;

/**
 * An table-driven parser implementing the LR parsing algorithm. Parses input in O(N) time
 */
public final class LRParser implements Parser{
    private final ParsingTable table;

    /**
     * Makes a parser given a {@link ParsingTable}
     * @param table the parsing table to use
     */
    public LRParser(ParsingTable table){
        this.table = table;
    }

    /**
     * Parses a string of tokens
     * @param tokens A string of tokens to be parsed
     * @return The parse tree if the tokens were parsed successfully, otherwise null
     */
    public ParseTree parse(Token[] tokens) {

        Deque<Integer> stateStack = new ArrayDeque<>();
        Deque<ParseTree> parseTreeStack = new ArrayDeque<>();

        // Start in state 0
        stateStack.push(0);

        int index = 0;
        while(index < tokens.length){
            int state = stateStack.peek();
            Token token = tokens[index];

            TableEntry entry = table.getAction(state, token.type);

            // Parse failed
            if(entry == null) return null;
            
            switch(entry.getAction()){
                case SHIFT:
                    // Update state stack and current token pointer
                    stateStack.push(((ShiftEntry)entry).getNextState());
                    index++;

                    // Update parse tree -- add new leaf node to stack
                    parseTreeStack.push(new ParseTree(token.type, token));
                    break;

                case ACCEPT: //Parse successful -- return parse tree
                    return parseTreeStack.getFirst();

                case REDUCE:
                    Rule reduceRule = ((ReduceEntry)entry).getRule();
                    String lhs = reduceRule.getLhs();

                    // Update state stack
                    for(int j = 0; j < reduceRule.getRhsSize(); j++) stateStack.pop();
                    GotoEntry gotoEntry = (GotoEntry)table.getGoto(stateStack.peek(), lhs);
                    stateStack.push(gotoEntry.getNextState());

                    // Update parse tree - merge nodes into parent node

                    if(reduceRule.getRhs().size() == 1) break;  //Simplify parse tree - remove unnecessary wrapping
                    ParseTree[] children = new ParseTree[reduceRule.getRhsSize()];
                    for(int j = reduceRule.getRhsSize() - 1; j >= 0; j--) children[j] = parseTreeStack.pop();
                    parseTreeStack.push(new ParseTree(lhs, children));
                    break;
                
                default:
            }
        }
        return null;
    }
}