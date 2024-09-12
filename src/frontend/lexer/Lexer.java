package frontend.lexer;

import frontend.Token;

import java.util.ArrayList;
import java.util.List;

public interface Lexer {
    Lex lex(String input);

    default List<Token> lexAll(String input) {
        return lexAll(input, true);
    }

    default List<Token> lexAll(String input, boolean includeEOF) {
        List<Token> tokens = new ArrayList<>();
        Lex lex = lex(input);

        do {
            tokens.add(lex.next());
        }
        while (!tokens.getLast().isEOF());

        if (!includeEOF) {
            tokens.removeLast();
        }
        return tokens;
    }
}
