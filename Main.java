import compiler.Token;
import compiler.lexer.Lexer;
import compiler.parsers.*;
import compiler.parsers.LRParsers.LRParser;
import compiler.parsers.LRParsers.parsing_table.ParsingTable;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) throws IOException {
	
		System.out.println("Lexing...");

		Lexer lexer = Lexer.fromFile("Code.lx");
		Lexer.Lex lex = lexer.lex(new String(Files.readAllBytes(Path.of("input.txt"))));

		List<Token> tkns = new ArrayList<>();
		Token tk = null;
		do {
			tk = lex.next();
			tkns.add(tk);
		} while(tk.type != "__END__");
		
		System.out.println("Generating Parser...");
		LRParser parse = new LRParser(ParsingTable.loadFromFile("Code.ptbl"));

		System.out.println("Parsing...");
		ParseTree pTree = parse.parse(tkns.toArray(Token[]::new));
		if(pTree == null) System.out.println("ERROR PARSING STRING");
		else System.out.println(pTree.prnt());
	}
}