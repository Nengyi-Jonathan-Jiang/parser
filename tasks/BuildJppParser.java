import compiler.Symbol;
import compiler.SymbolTableReader;
import compiler.grammar.Grammar;
import compiler.grammar.GrammarReader;
import compiler.parser.parser_generator.LR1ParseTableBuilder;

import java.io.File;

public class BuildJppParser {
    public static void main(String[] args) {
        System.out.println(new File("./").getAbsolutePath());

        System.out.println("Generating Symbol Table...");

        var symbolTable = Symbol.SymbolTable.merge(
            SymbolTableReader.generateFromLexerFile("/je++/je++.lex"),
            SymbolTableReader.generateFromGrammarFile("/je++/je++.bnf")
        );

        System.out.println(symbolTable);

        System.out.println("Generating Je++ Grammar ...");
        Grammar grammar = GrammarReader.readFromFile(symbolTable, "/je++/je++.bnf");

        System.out.println("===================");
        System.out.println(grammar);
        System.out.println("===================");

        System.out.println("Generating LR(1) Parsing Table ...");
        new LR1ParseTableBuilder(grammar).getTable().saveToFile("./res/je++/generated/je++.ptbl");
    }
}