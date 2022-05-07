import compiler.parsers.*;

public class Main {
	public static void main(String[] args) {
		System.out.println("Generating Parser...");
		LRParser parse = new LRParserFromFile("Code.ptbl");

		System.out.println("Parsing...");

		String str = "IDENTIFIER = IDENTIFIER + NUMBER_CONST ; __END__";

		ParseTree pTree = parse.parse(str.split(" "), true);
		if(pTree == null) System.out.println("ERROR PARSING STRING");
		else System.out.println(pTree.prnt());
	}
}