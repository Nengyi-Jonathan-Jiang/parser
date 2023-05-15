import compiler.frontend.SymbolTableReader;
import compiler.frontend.grammar.Grammar;
import compiler.frontend.grammar.GrammarReader;
import compiler.frontend.parsers.LRParsers.LR1ParseTableBuilder;

public class GenerateParsingTable {
    public static void main(String[] args){
		var symbolTable = SymbolTableReader.generateFromGrammarFile("Crab.ebnf");

		System.out.println(symbolTable.size());

        System.out.println("Generating Grammar...");

		Grammar grammar = GrammarReader.readFromFile(symbolTable, "Crab.ebnf");

		System.out.println("===================");
		System.out.println(grammar);
		System.out.println("===================");

        System.out.println("Generating Parser...");

		new LR1ParseTableBuilder(grammar).getTable().saveToFile("Code.ptbl");
    }
}
