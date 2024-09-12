package frontend.lexer;

import frontend.Symbol;
import frontend.Token;
import util.Helpers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

// A .lx file follows the following grammar:

// lexer-file := [ lex-rule ]
// lex-rule  := "ignore" regex
//            | symbol-name ":=" regex
//            | symbol-name
//
// where
//
// symbol-name can be a sequence of letters and/or hyphens or a
// quoted string, and regex is any sequence of characters excluding
// newlines

public class RegexBasedLexerReader {
    private RegexBasedLexerReader() {
    }

    private static final Lexer lexerLexer = Helpers.evaluate(() -> {
        Symbol.SymbolTable table = new Symbol.SymbolTable();
        RegexBasedLexer baseLexerLexer = new RegexBasedLexer(table);

        baseLexerLexer.addIgnored("//[^\\n]*");
        baseLexerLexer.addRule(table.create("ignore-rule"), "ignore\\s[^\\n]+\\n");
        baseLexerLexer.addRule(table.create("symbol-name"), "\"([^\"\\\\\\s]|\\\\[^\\s])*\"|[\\w-]+");
        baseLexerLexer.addRule(table.create("newline"), "\\s*\\n\\s*");
        baseLexerLexer.addRule(table.create("match-regex"), ":=\\s[^\\n]+\\n");

        return new ComposedLexer(baseLexerLexer,
            token -> token.mapIf(
                t -> t.typeNameEquals("symbol-name") && t.value.startsWith("\""),
                value -> value.substring(1, value.length() - 1)
            ).mapIf(
                t -> t.typeNameEquals("match-regex"),
                value -> value.substring(2).trim()
            ).mapIf(
                t -> t.typeNameEquals("ignore-rule"),
                value -> value.substring(6).trim()
            )
        );
    });

    public static Lexer readLexerFromString(String str, Symbol.SymbolTable symbolTable) throws LexerReaderException {
        List<Symbol> ignoredSymbols = new ArrayList<>();
        RegexBasedLexer result = new RegexBasedLexer(symbolTable);

        Queue<Token> tokens = new ArrayDeque<>(lexerLexer.lexAll(str + "\n"));
        while (!tokens.isEmpty()) {
            Token currentToken = tokens.remove();
            switch (currentToken.typeName()) {
                case "symbol-name" -> {
                    String symbolName = currentToken.value;
                    boolean isLiteralRule = tokens.isEmpty() || !tokens.peek().typeNameEquals("match-regex");

                    String regex = isLiteralRule
                        ? symbolName.replaceAll("[\\[.*+(\\-)?{|}^$\\\\\\]]", "\\\\$0")
                        : tokens.remove().value;

                    result.addRule(symbolTable.create(symbolName), regex);
                }
                case "ignore-rule" -> {
                    String regex = currentToken.value;

                    result.addIgnored(regex);
                }
            }
        }

        return result;
    }
}
