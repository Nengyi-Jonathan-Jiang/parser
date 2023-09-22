package frontend.parser.lr_parser;

import java.util.*;
import java.util.stream.Collectors;

import frontend.parser.Rule;
import frontend.Symbol;
import frontend.Token;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import frontend.parser.lr_parser.parsing_table.*;

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
    public ParseTreeNode parse(Token[] tokens) {
        Parse p = start();
        for(Token token : tokens){
            p.process(token);
        }
        if(!p.isFinished()) return null;
        return p.getParseTree();
    }

    public Parse start(){
        return new Parse(this);
    }

    public static class Parse implements Parser.Parse{
        private final Deque<Integer> stateStack = new ArrayDeque<>();
        private final Deque<ParseTreeNode> parseTreeNodeStack = new ArrayDeque<>();
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
                if(entry == null) {
                    throw new LRParsingError("Parse failed: expected one of "
                        + table.acceptableSymbolsAtState(state).stream().map(Symbol::toString).collect(Collectors.joining(" "))
                        + ", instead got " + token,
                        table.acceptableSymbolsAtState(state)
                    );
                }

                switch (entry.getAction()) {
                    case SHIFT -> {
                        // Update state stack and current token pointer
                        stateStack.push(((ShiftEntry) entry).nextState());

                        // Update parse tree -- add new leaf node to stack
                        parseTreeNodeStack.push(new ParseTreeNode(token.type, token));
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
                        ParseTreeNode[] children = new ParseTreeNode[reduceRule.getRhsSize()];
                        for (int j = reduceRule.getRhsSize() - 1; j >= 0; j--) children[j] = parseTreeNodeStack.pop();
                        parseTreeNodeStack.push(new ParseTreeNode(lhs, children));
                    }
                }
            }
        }

        public ParseTreeNode getParseTree(){
            return parseTreeNodeStack.getLast();
        }

        public boolean isFinished(){
            return finished;
        }

        public Deque<ParseTreeNode> getParseTreeStack() {
            return parseTreeNodeStack;
        }
    }
}