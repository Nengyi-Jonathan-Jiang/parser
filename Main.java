import compiler.parsers.*;
import compiler.parsers.LRParsers.LRParser;
import compiler.parsers.LRParsers.parsing_table.ParsingTable;

public class Main {
	public static void main(String[] args) {
		System.out.println("Generating Parser...");
		LRParser parse = new LRParser(ParsingTable.loadFromFile("Code.ptbl"));

		System.out.println("Parsing...");

		// IDENTIFIER FUNC IDENTIFIER ( ) { IDENTIFIER VAR IDENTIFIER ; INPUT IDENTIFIER ; TEST ( IDENTIFIER != NUMBER_CONST ) { TEST ( IDENTIFIER ) { CASE ( IDENTIFIER == NUMBER_CONST ) { OUTPUT STRING_CONST; } CASE ( IDENTIFIER % 2 == NUMBER_CONST ) { OUTPUT HEX_CONST ; } DEFAULT { OUTPUT STRING_CONST ; } } } OUTPUT NUMBER_CONST ; }

//		String str = "IDENTIFIER = IDENTIFIER + NUMBER_CONST ; __END__";
		String str =
				"IDENTIFIER FUNC IDENTIFIER ( ) { "
			+ 		"IDENTIFIER VAR IDENTIFIER ; "
			+ 		"INPUT IDENTIFIER ; "
			+ 		"TEST ( IDENTIFIER != NUMBER_CONST ) { "
			+ 			"CASE ( IDENTIFIER == NUMBER_CONST ) { "
			+	 			"OUTPUT STRING_CONST ; "
			+ 			"} "
			+ 			"CASE ( IDENTIFIER % NUMBER_CONST == NUMBER_CONST ) { "
			+ 				"OUTPUT HEX_CONST ; "
			+ 			"} "
			+			"DEFAULT { "
			+ 				"OUTPUT STRING_CONST ; "
			+ 			"} "
			+ 		"} "
			+ 		"OUTPUT NUMBER_CONST ; "
			+	"} "
			+	"__END__";

		ParseTree pTree = parse.parse(str.split(" "));
		if(pTree == null) System.out.println("ERROR PARSING STRING");
		else System.out.println(pTree.prnt());
	}
}