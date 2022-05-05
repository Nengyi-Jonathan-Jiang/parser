package parser.parsers;

import parser.parsingTable.*;
import parser.*;
import parser.grammar.*;
import java.util.*;

public abstract class Parser {
    protected ParsingTable table;

    public Parser(Grammar grammar){
        generateParsingTable(grammar);
    }

    public ParseTree parse(String[] tokens){return parse(tokens, false);}

    public ParseTree parse(String[] tokens, boolean debug){

        Deque<Integer> stateStack = new ArrayDeque<>(); //Stack of states
        Deque<ParseTree> tknStack = new ArrayDeque<>(); //Stack of parse trees nodes
        stateStack.push(0); // Start in state 0

        int index = 0;
        while(index < tokens.length){
            int state = stateStack.peek();
            String tkn = tokens[index];

            TableEntry entry = table.getAction(state, tkn);

            // Parse failed
            if(entry == null){
                if(debug) System.out.println("Could not parse string!");
                return null;
            }
            
            
            if(debug) System.out.print(String.format("%-20s", tknStack.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ")));

            switch(entry.getAction()){
                case SHIFT:
                    if(debug) System.out.println("SHIFT \"" + tkn + "\"");

                    // Update state stack and current token pointer
                    stateStack.push(((ShiftEntry)entry).getNextState());
                    index++;

                    // Update parse tree -- add new leaf node to stack
                    tknStack.push(new ParseTree(tkn));
                    break;

                case ACCEPT: //Parse successful -- return parse tree
                    if(debug) System.out.println("ACCEPTED INPUT");
                    return tknStack.getFirst();

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
                    for(int j = reduceRule.getRhsSize() - 1; j >= 0; j--) children[j] = tknStack.pop();
                    tknStack.push(new ParseTree(lhs, children));
                    break;
                
                default:
            }
        }
        return null;
    }

    protected abstract ParsingTable generateParsingTable(Grammar grammar);
}
