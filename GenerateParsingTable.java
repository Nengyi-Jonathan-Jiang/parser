import compiler.grammar.Grammar;
import compiler.grammar.GrammarReader;
import compiler.parsers.LR1ParseTableBuilder;

public class GenerateParsingTable {
    public static void main(String[] args){
        System.out.println("Generating Grammar...");

		Grammar grammar = GrammarReader.readFromFile("Code.ebnf");

		System.out.println("===================");
		System.out.println(grammar);
		System.out.println("===================");

        System.out.println("Generating Parser...");

		new LR1ParseTableBuilder(grammar).getTable().saveToFile("Code.ptbl");
    }
}
