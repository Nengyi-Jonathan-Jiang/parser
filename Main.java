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

		ArrayList<Token> tkns = new ArrayList<>();

		if(Math.sqrt(1) > 2){
			Scanner scan;
			try {
				scan = new Scanner(new File("TokenStream.txt"));
			} catch (Exception e) {
				System.out.println("Could not read file TokenStream.txt");
				return;
			}
			while (scan.hasNext()) {
				String tk = scan.next();
				tkns.add(new Token(tk, tk, -1));
			}
			scan.close();
		}
		else{	
			System.out.println("Lexing...");

			Lexer lexer = Lexer.fromFile("Code.lx");
			Lexer.Lex lex = lexer.lex(new String(Files.readAllBytes(Path.of("input.txt"))));

			Token tk = null;
			do {
				tk = lex.next();
				tkns.add(tk);
			} while(tk.type != "__END__");
		}

		System.out.println(tkns.stream().map(i->i.toString()).collect(Collectors.joining(" ")));

		System.out.println("Generating Parser...");
		LRParser parse = new LRParser(ParsingTable.loadFromFile("Code.ptbl"));

		System.out.println("Parsing...");

		ParseTree pTree = parse.parse(tkns.toArray(Token[]::new));
		if(pTree == null) System.out.println("ERROR PARSING STRING");
		else System.out.println(pTree.prnt());
	}
}