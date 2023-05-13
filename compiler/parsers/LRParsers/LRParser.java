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
        Parse p = getParse();
        for(Token token : tokens){
            p.process(token);
        }
        if(!p.isFinished()){
            return null;
        }
        return p.getParseTree();
    }

    public Parse getParse(){
        return new Parse(this);
    }

    private static class Parse implements Parser.Parse{
        private final Deque<Integer> stateStack = new ArrayDeque<>();
        private final Deque<ParseTree> parseTreeStack = new ArrayDeque<>();
        private final ParsingTable table;
        private boolean finished;

        public Parse(LRParser parser){
            stateStack.push(0);
            table = parser.table;
            finished = false;
        }

        public void process(Token token){
            while(true){
                //noinspection DataFlowIssue
                int state = stateStack.peek();

                TableEntry entry = table.getAction(state, token.type);

                // Parse failed
                if(entry == null) throw new Error("Parse failed!");

                switch (entry.getAction()) {
                    case SHIFT -> {
                        // Update state stack and current token pointer
                        stateStack.push(((ShiftEntry) entry).nextState());

                        // Update parse tree -- add new leaf node to stack
                        parseTreeStack.push(new ParseTree(token.type, token));
                        return;
                    }
                    case ACCEPT -> { //Parse successful -- return parse tree
                        finished = true;
                        return;
                    }
                    case REDUCE -> {
                        Rule reduceRule = ((ReduceEntry) entry).rule();
                        Symbol lhs = reduceRule.getLhs();

                        // Update state stack
                        for (int j = 0; j < reduceRule.getRhsSize(); j++) stateStack.pop();
                        GotoEntry gotoEntry = (GotoEntry) table.getGoto(stateStack.peek(), lhs);
                        stateStack.push(gotoEntry.nextState());

                        // Update parse tree - merge nodes into parent node

                        if (reduceRule.getRhs().size() == 1) break;  //Simplify parse tree - remove unnecessary wrapping
                        ParseTree[] children = new ParseTree[reduceRule.getRhsSize()];
                        for (int j = reduceRule.getRhsSize() - 1; j >= 0; j--) children[j] = parseTreeStack.pop();
                        parseTreeStack.push(new ParseTree(lhs, children));
                    }
                }
            }
        }

        public ParseTree getParseTree(){
            return parseTreeStack.getLast();
        }
        public boolean isFinished(){
            return finished;
        }
    }
}