package frontend.parser.lr_parser;

import frontend.Symbol;
import frontend.Token;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import frontend.parser.lr_parser.parsing_table.ActionTableEntry;
import frontend.parser.lr_parser.parsing_table.ParsingTable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An table-driven parser implementing the LR parsing algorithm. Parses input in O(N) time
 */
public final class LRParser implements Parser {
    private final ParsingTable table;

    /**
     * Makes a parser given a {@link ParsingTable}
     *
     * @param table the parsing table to use
     */
    public LRParser(ParsingTable table) {
        this.table = table;
    }

    /**
     * Parses a string of tokens
     *
     * @param tokens A string of tokens to be parsed
     * @return The parse tree if the tokens were parsed successfully, otherwise null
     */
    public ParseTreeNode parse(Token[] tokens) {
        Parse p = start();
        for (Token token : tokens) {
            p.process(token);
        }
        if (!p.didAccept()) return null;
        return p.getResult();
    }

    public Parse start() {
        return new Parse(this);
    }

    public static class Parse implements Parser.Parse {
        private final Deque<Integer> stateStack = new ArrayDeque<>();
        private final Deque<ParseTreeNode> parseTreeNodeStack = new ArrayDeque<>();
        private final ParsingTable table;
        private boolean didAccept;

        private final List<LRParsingError> errors = new ArrayList<>();

        public Parse(LRParser parser) {
            stateStack.push(0);
            table = parser.table;
            didAccept = false;
        }

        public boolean process(Token token) {
            while (true) {
                //noinspection DataFlowIssue
                int currentState = stateStack.peek();

                ActionTableEntry entry = table.getAction(currentState, token.type);
                if (entry == null) {
                    Set<Symbol> acceptedSymbols = table.acceptableSymbolsAtState(currentState);
                    String message =
                        "Parse failed: expected one of "
                            + acceptedSymbols.stream().map(Symbol::toString).collect(Collectors.joining(" "))
                            + ", instead got "
                            + token;

                    errors.add(new LRParsingError(message, acceptedSymbols));

                    return false;
                }

                entry.applyAction(stateStack, parseTreeNodeStack, table, token);

                didAccept |= entry.isAccepting();
                if (entry.isDoneProcessingToken()) break;
            }

            return didAccept;
        }

        public ParseTreeNode getResult() {
            if (errors.isEmpty()) {
                return parseTreeNodeStack.peek();
            }

            // Something went wrong while parsing. Throw all the accumulated errors
            var err = new RuntimeException("Parse failed");
            errors.forEach(err::addSuppressed);

            throw err;
        }

        public boolean didAccept() {
            return didAccept;
        }

        public Collection<ParseTreeNode> getParseTreeStack() {
            return parseTreeNodeStack;
        }
    }
}