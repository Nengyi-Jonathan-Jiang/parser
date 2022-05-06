import java.util.*;
import java.util.stream.*;

import compiler.Rule;
import compiler.grammar.Grammar;
import compiler.grammar.GrammarReader;
import compiler.parsers.*;

public class Main {
	public static void main(String[] args) {
		System.out.println("Generating Grammar...");

		Grammar gram = GrammarReader.readFromFile("Grammar.ebnf");

		System.out.println("===================");
		System.out.println(gram);
		System.out.println("===================");

		// Parser parse = new SLRParser(gram);
		// Parser parse = new LR0Parser(gram);
		System.out.println("Generating Parser...");

		LRParser parse = new LR1Parser(gram);
		// LRParser parse = new SLRParser(gram);

		System.out.println("Parsing...");

		String str = "id = id ; __END__";

		ParseTree pTree = parse.parse(str.split(" "), true);
		if(pTree == null) System.out.println("ERROR PARSING STRING");
		else System.out.println(pTree.prnt());
	}
	
	public static String prnt(Collection<?> c){
	    return c.stream().map(i->"\"" + i + "\"").collect(Collectors.toList()).toString();
	}
}
