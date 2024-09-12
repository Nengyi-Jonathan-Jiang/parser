package frontend.lexer;

import frontend.Token;

import java.util.function.UnaryOperator;

/**
 * A class wrapping the output of a lexer with a mapping operation.
 */
public class ComposedLexer implements Lexer {
    private final Lexer baseLexer;
    private final UnaryOperator<Token> mappingFunction;

    /**
     * @param baseLexer The lexer to be wrapped
     * @param mappingFunction A function that takes in a token and outputs another token.
     *                        The function will not be called on EOF tokens.
     */
    public ComposedLexer(Lexer baseLexer, UnaryOperator<Token> mappingFunction) {
        this.baseLexer = baseLexer;
        this.mappingFunction = mappingFunction;
    }

    @Override
    public Lex lex(String input) {
        return new ComposedLex(input);
    }

    private class ComposedLex implements Lex {
        private final Lex baseLex;

        private ComposedLex(String input) {
            baseLex = baseLexer.lex(input);
        }

        @Override
        public Token next() {
            Token result = baseLex.next();
            if(result.isEOF()) {
                return result;
            }
            return mappingFunction.apply(result);
        }
    }
}
