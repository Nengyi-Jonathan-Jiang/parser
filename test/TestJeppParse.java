import frontend.Token;
import jepp.frontend.JePPFrontend;
import jepp.interpreter.Interpreter;
import frontend.lexer.Lexer;
import frontend.parser.*;
import org.junit.Test;

import java.io.*;

public class TestJeppParse {
	@Test
	public void test() {
		String input;
		try(InputStream jeppFile = TestJeppParse.class.getResourceAsStream("/test/hello world.jepp")) {
            assert jeppFile != null;
			input = new String(jeppFile.readAllBytes());
		} catch(Exception e) { throw new RuntimeException(e); }

		Lexer.Lex lex = JePPFrontend.tokenize(input);
		Parser.Parse parse = JePPFrontend.startParse();
		System.out.println("Processing input...");
		Token tk;
		do {
			tk = lex.next();
			parse.process(tk);
			System.out.print(tk + " ");
		} while(!tk.isEOF());
		System.out.println();

		ParseTreeNode pTree;
		pTree = parse.getParseTree();
		if(pTree == null) throw new Error("unknown error parsing string");

		Interpreter interpreter = new Interpreter();
		interpreter.run(pTree);
		String result = interpreter.result();
		System.out.println("--------------------\n" + result);
	}
}