package jpp.assembler;

import compiler.SymbolTableReader;
import compiler.grammar.Grammar;
import compiler.grammar.GrammarReader;
import compiler.parser.parser_generator.LR1ParseTableBuilder;

public class genparser {
    public static void main(String[] args){
        var symbolTable = SymbolTableReader.generateFromGrammarFile(Assembler.ebnfFile);

        System.out.println(symbolTable);

        Grammar grammar = GrammarReader.readFromFile(symbolTable, Assembler.ebnfFile);

        System.out.println("Generating Parser...");

        new LR1ParseTableBuilder(grammar).getTable().saveToFile(Assembler.ptblFile);
    }
}
