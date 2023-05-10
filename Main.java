import compiler.Symbol;
import compiler.SymbolTableReader;
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
	public static void main(String[] args) throws Exception {

		System.out.println("Generating Symbol Table...");

		var symbolTable = Symbol.SymbolTable.merge(
			SymbolTableReader.generateFromLexerFile("Code.lx"),
			SymbolTableReader.generateFromLexerFile("Code.ebnf")
		);

		System.out.println(symbolTable);

		System.out.println("Lexing...");

		Lexer lexer = Lexer.fromFile(symbolTable, "Code.lx");
		Lexer.Lex lex = lexer.lex(new String(Files.readAllBytes(Path.of("input.txt"))));

		PrintWriter tokenStreamFile = new PrintWriter("TokenStream.txt");

		List<Token> tkns = new ArrayList<>();
		Token tk;
		do {
			tk = lex.next();
			tkns.add(tk);
			tokenStreamFile.print(tk + " ");
		} while(tk.type != symbolTable.__END__);
		tokenStreamFile.close();
		
		System.out.println("Generating Parser...");
		LRParser parse = new LRParser(ParsingTable.loadFromFile(symbolTable, "Code.ptbl"));

		System.out.println("Parsing...");
		ParseTree pTree = parse.parse(tkns.toArray(Token[]::new));
		if(pTree == null) throw new Error("ERROR PARSING STRING");
		System.out.println(pTree.prnt());

		Interp interp = new Interp();
		interp.run(pTree);
		String result = interp.result();
		System.out.println("--------------------\n" + result);
		FileWriter output = new FileWriter("output.txt");
		output.write(result);
		output.close();
	}
}