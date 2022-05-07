import java.util.*;
import java.util.stream.*;

import compiler.grammar.Grammar;
import compiler.grammar.GrammarReader;
import compiler.items.Item;
import compiler.parsers.*;
import compiler.*;

public class Main {
	public static void main(String[] args) {
		System.out.println("Generating Grammar...");

		Grammar gram = GrammarReader.readFromFile("Code.ebnf");

		System.out.println("===================");
		System.out.println(gram);
		System.out.println("===================");

		System.out.println("Generating Parser...");

		// Parser parse = new LR0Parser(gram);
		// Parser parse = new SLRParser(gram);
		LRParser parse = new LR1Parser(gram);

		parse.saveParsingTableToFile("Code.ptbl");

		System.out.println("Parsing...");

		String str = "IDENTIFIER = IDENTIFIER + NUMBER_CONST ; __END__";

		ParseTree pTree = parse.parse(str.split(" "), true);
		if(pTree == null) System.out.println("ERROR PARSING STRING");
		else System.out.println(pTree.prnt());
	}
	
	public static String prnt(Collection<?> c){
	    return c.stream().map(i->"\"" + i + "\"").collect(Collectors.toList()).toString();
	}
}
