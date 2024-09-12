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
        return new Parse();
    }

    public class Parse implements Parser.Parse {
        private final Stack<Integer> stateStack = new Stack<>();
        private final Stack<ParseTreeNode> parseTreeNodeStack = new Stack<>();
        private boolean didAccept;

        private final List<LRParsingError> errors = new ArrayList<>();

        private Parse() {
            stateStack.push(0);
            didAccept = false;
        }

        public void process(Token token) {
            while (true) {
                int currentState = stateStack.peek();

                ActionTableEntry entry = table.getAction(currentState, token.type);
                if (entry == null) {
                    addErrorOn(token, currentState);
                    break;
                }

                entry.applyAction(stateStack, parseTreeNodeStack, table, token);

                if (entry.isAccepting()) {
                    didAccept = true;
                }

                if (entry.isDoneProcessingToken()) {
                    break;
                }
            }
        }

        private void addErrorOn(Token token, int currentState) {
            Set<Symbol> acceptedSymbols = table.acceptableSymbolsAtState(currentState);
            String message =
                "Expected one of "
                    + acceptedSymbols.stream().map(Symbol::toString).collect(Collectors.joining(" "))
                    + ", instead got "
                    + token + " at " + token.startLocation;

            errors.add(new LRParsingError(message, acceptedSymbols));
        }

        public ParseTreeNode getResult() {
            if (!errors.isEmpty()) {
                RuntimeException exception = new RuntimeException("Parse failed");
                errors.forEach(exception::addSuppressed);
                throw exception;
            }

            return parseTreeNodeStack.peek();
        }

        public boolean didAccept() {
            return didAccept;
        }

        public Collection<ParseTreeNode> getParseTreeStack() {
            return parseTreeNodeStack;
        }
    }
}