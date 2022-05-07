import compiler.grammar.Grammar;
import compiler.grammar.GrammarReader;
import compiler.parsers.LR1Parser;
import compiler.parsers.LRParser;
import compiler.parsers.LRParserFromFile;

public class GenerateParsingTable {
    public static void main(String[] args){
        System.out.println("Generating Grammar...");

		Grammar grammar = GrammarReader.readFromFile("Code.ebnf");

		System.out.println("===================");
		System.out.println(grammar);
		System.out.println("===================");

        System.out.println("Generating Parser...");

		LRParser parse = new LR1Parser(grammar);

		parse.saveParsingTableToFile("Code.ptbl");
    }
}
