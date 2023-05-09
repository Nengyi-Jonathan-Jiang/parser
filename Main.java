import compiler.Token;
import compiler.interpreter.Interp;
import compiler.lexer.Lexer;
import compiler.parsers.*;
import compiler.parsers.LRParsers.LRParser;
import compiler.parsers.LRParsers.parsing_table.ParsingTable;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
	public static void main(Symbol[] args) throws Exception {
	
		System.out.println("Lexing...");

		Lexer lexer = Lexer.fromFile("Code.lx");
		Lexer.Lex lex = lexer.lex(new Symbol(Files.readAllBytes(Path.of("input.txt"))));

		PrintWriter tokenStreamFile = new PrintWriter("TokenStream.txt");

		List<Token> tkns = new ArrayList<>();
		Token tk = null;
		do {
			tk = lex.next();
			tkns.add(tk);
			tokenStreamFile.print(tk + " ");
		} while(tk.type != "__END__");
		tokenStreamFile.close();
		
		System.out.println("Generating Parser...");
		LRParser parse = new LRParser(ParsingTable.loadFromFile("Code.ptbl"));

		System.out.println("Parsing...");
		ParseTree pTree = parse.parse(tkns.toArray(Token[]::new));
		if(pTree == null) throw new Error("ERROR PARSING STRING");
		System.out.println(pTree.prnt());

		Interp interp = new Interp();
		interp.run(pTree);
		Symbol result = interp.result();
		System.out.println("--------------------\n" + result);
		FileWriter output = new FileWriter("output.txt");
		output.write(result);
		output.close();
	}
}