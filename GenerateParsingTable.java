import compiler.SymbolTableReader;
import compiler.grammar.Grammar;
import compiler.grammar.GrammarReader;
import compiler.parsers.LRParsers.LR1ParseTableBuilder;

public class GenerateParsingTable {
    public static void main(String[] args){
        System.out.println("Generating Grammar...");

		var symbolTable = SymbolTableReader.generateFromGrammarFile("Code.ebnf");

		Grammar grammar = GrammarReader.readFromFile(symbolTable, "Code.ebnf");

		System.out.println("===================");
		System.out.println(grammar);
		System.out.println("===================");

        System.out.println("Generating Parser...");

		new LR1ParseTableBuilder(grammar).getTable().saveToFile("Code.ptbl");
    }
}
