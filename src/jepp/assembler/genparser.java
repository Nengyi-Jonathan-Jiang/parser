package jepp.assembler;

import frontend.SymbolTableReader;
import frontend.grammar.Grammar;
import frontend.grammar.GrammarReader;
import frontend.parser.parser_generator.LR1ParseTableBuilder;

public class genparser {
    public static void main(String[] args){
        var symbolTable = SymbolTableReader.generateFromGrammarFile(Assembler.ebnfFile);

        System.out.println(symbolTable);

        Grammar grammar = GrammarReader.readFromFile(symbolTable, Assembler.ebnfFile);

        System.out.println("Generating Parser...");

        new LR1ParseTableBuilder(grammar).getTable().saveToFile(Assembler.ptblFile);
    }
}
