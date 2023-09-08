import compiler.Token;
import jepp.JePPFrontend;
import jepp.interpreter.Interpreter;
import compiler.lexer.Lexer;
import compiler.parser.*;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class TestJeppParse {
	@Test
	public void test() throws IOException {

		Lexer.Lex lex = JePPFrontend.tokenize(new String(TestJeppParse.class.getResourceAsStream("/test/hello world.jepp").readAllBytes()));

		System.out.println("Lexing...");
		List<Token> tkns = new ArrayList<>();
		Token tk;
		do {
			tk = lex.next();
			tkns.add(tk);
			System.out.print(tk + " ");
		} while(!tk.isEOF());

		System.out.println("\nParsing...");

		ParseTreeNode pTree;
		pTree = JePPFrontend.parse(tkns.toArray(Token[]::new));
		if(pTree == null) throw new Error("ERROR PARSING STRING");

		Interpreter interpreter = new Interpreter();
		interpreter.run(pTree);
		String result = interpreter.result();
		System.out.println("--------------------\n" + result);
	}
}