package parser;
import java.util.*;
import java.util.stream.*;

import parser.grammar.Grammar;

import parser.parsers.*;

public class Main {
	public static void main(String[] args) {
		Grammar gram = new Grammar(Arrays.asList(
		  //  new Rule("E", "E","*","T"),
		  //  new Rule("E", "T"),
		  //  new Rule("T", "T","+","F"),
		  //  new Rule("T", "F"),
		  //  new Rule("F", "(","E",")"),
		  //  new Rule("F", "id")

		    // new Rule("E", "E","+","T"),
            // new Rule("E", "T"),
            // new Rule("T", "(","E",")"),
            // new Rule("T", "id")

			new Rule("E", "E", "+", "T"),
			new Rule("E", "V", "=", "E"),
			new Rule("E", "T"),
			new Rule("T", "(", "E", ")"),
			new Rule("T", "id"),
			new Rule("V", "id")
		), "E");

		LR0Parser parse = new LR0Parser(gram);

		parse.parse(new String[]{
			"id", "=", "id", "+", "(", "id", ")",
		});
	}
	
	public static String prnt(Collection<?> c){
	    return c.stream().map(i->"\"" + i + "\"").collect(Collectors.toList()).toString();
	}
}
