package jepp.frontend;

import frontend.Symbol;
import frontend.SymbolTableReader;
import frontend.lexer.Lex;
import frontend.lexer.Lexer;
import frontend.lexer.RegexBasedLexer;
import frontend.parser.Parser;
import frontend.parser.lr_parser.LRParser;
import frontend.parser.lr_parser.parsing_table.ParsingTable;

public class JePPFrontend {
    private static final Lexer lexer;
    private static final Parser parser;
    public static final Symbol.SymbolTable symbolTable;

    public static Lex beginLex(String input) {
        return lexer.lex(input);
    }

    static {
        symbolTable = Symbol.SymbolTable.merge(
            SymbolTableReader.generateFromLexerFile("je++/je++.lex"),
            SymbolTableReader.generateFromGrammarFile("je++/je++.bnf")
        );
        symbolTable.lock();

        System.out.println("Loading Je++ lexer ...");
        lexer = RegexBasedLexer.fromFile(symbolTable, "je++/je++.lex");
        System.out.println("Loading Je++ parser ...");
        parser = new LRParser(ParsingTable.loadFromFile(symbolTable, "je++/generated/je++.ptbl"));
    }

    public static Parser.Parse beginParse() {
        return parser.start();
    }

    public static Symbol getSymbol(String s) {
        return symbolTable.get(s);
    }
}