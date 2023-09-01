import compiler.SymbolTableReader;
import compiler.grammar.Grammar;
import compiler.grammar.GrammarReader;
import compiler.parser.parser_generator.LR1ParseTableBuilder;

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
