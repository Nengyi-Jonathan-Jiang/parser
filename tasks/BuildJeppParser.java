import frontend.Symbol;
import frontend.SymbolTableReader;
import frontend.grammar.ContextFreeGrammar;
import frontend.grammar.ContextFreeGrammarReader;
import frontend.parser.parser_generator.LR1ParseTableBuilder;

import java.io.File;

public class BuildJeppParser {
    public static void main(String[] args) {
        System.out.println("Generating Symbol Table...");

        var symbolTable = Symbol.SymbolTable.merge(
            SymbolTableReader.generateFromLexerFile("je++/je++.lex"),
            SymbolTableReader.generateFromGrammarFile("je++/je++.bnf")
        );

        System.out.println(symbolTable);

        System.out.println("Generating Je++ Grammar ...");
        ContextFreeGrammar grammar = ContextFreeGrammarReader.readFromFile(symbolTable, "je++/je++.bnf");

        System.out.println("===================");
        System.out.println(grammar);
        System.out.println("===================");

        System.out.println("Generating LR(1) Parsing Table ...");
        new LR1ParseTableBuilder(grammar).getTable().saveToFile("./res/je++/generated/je++.ptbl");
    }
}