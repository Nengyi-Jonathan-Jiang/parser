package compiler.assembler;

import compiler.frontend.SymbolTableReader;
import compiler.frontend.grammar.Grammar;
import compiler.frontend.grammar.GrammarReader;
import compiler.frontend.parsers.LRParsers.LR1ParseTableBuilder;

public class genparser {
    public static void main(String[] args){
        var symbolTable = SymbolTableReader.generateFromGrammarFile(Assembler.ebnfFile);

        System.out.println(symbolTable);

        Grammar grammar = GrammarReader.readFromFile(symbolTable, Assembler.ebnfFile);

        System.out.println("Generating Parser...");

        new LR1ParseTableBuilder(grammar).getTable().saveToFile(Assembler.ptblFile);
    }
}
