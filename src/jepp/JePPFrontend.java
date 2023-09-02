package jepp;

import compiler.Symbol;
import compiler.SymbolTableReader;
import compiler.Token;
import compiler.lexer.Lexer;
import compiler.parser.ParseTreeNode;
import compiler.parser.Parser;
import compiler.parser.lr_parser.LRParser;
import compiler.parser.lr_parser.parsing_table.ParsingTable;

public class JePPFrontend {
    private static final Lexer lexer;
    private static final Parser parser;
    private static final Symbol.SymbolTable symbolTable;

    public static Lexer.Lex tokenize(String input) {
        return lexer.lex(input);
    }

    static {
        symbolTable = Symbol.SymbolTable.merge(
            SymbolTableReader.generateFromLexerFile("/je++/je++.lex"),
            SymbolTableReader.generateFromGrammarFile("/je++/je++.bnf")
        );
//        symbolTable.lock();

        System.out.println("Loading Je++ lexer ...");
        lexer = Lexer.fromFile(symbolTable, "/je++/je++.lex");
        System.out.println("Loading Je++ parser ...");
        parser = new LRParser(ParsingTable.loadFromFile(symbolTable, "/je++/generated/je++.ptbl"));
    }

    public static ParseTreeNode parse(Token[] tokens) {
        return parser.parse(tokens);
    }
}