package parser;
import java.util.*;
import java.util.stream.*;

import parser.grammar.Grammar;

import parser.parsers.*;

public class Main {
	public static void main(String[] args) {
		Grammar gram = new Grammar(Arrays.asList(
		   new Rule("E", "E","*","T"),
		   new Rule("E", "V","=","T"),
		   new Rule("E", "T"),
		   new Rule("T", "T","+","F"),
		   new Rule("T", "F"),
		   new Rule("F", "(","E",")"),
		   new Rule("F", "id"),
		   new Rule("V", "id")

		    // new Rule("E", "E","+","T"),
            // new Rule("E", "T"),
            // new Rule("T", "(","E",")"),
            // new Rule("T", "id")

			// new Rule("E", "E", "+", "T"),
			// new Rule("E", "V", "=", "E"),
			// new Rule("E", "T"),
			// new Rule("T", "(", "E", ")"),
			// new Rule("T", "id"),
			// new Rule("V", "id")
		), "E");

		LR0Parser parse = new LR0Parser(gram);

		String str = "id = id + ( id + id ) * ( id ) __END__";
		// String str = "id = id + id __END__";

		ParseTree pTree = parse.parse(str.split(" "));
		System.out.println(pTree.prnt());
	}
	
	public static String prnt(Collection<?> c){
	    return c.stream().map(i->"\"" + i + "\"").collect(Collectors.toList()).toString();
	}
}
