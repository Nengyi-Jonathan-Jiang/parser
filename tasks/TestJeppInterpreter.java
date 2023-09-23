import frontend.Token;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import frontend.lexer.Lexer;
import jepp.frontend.JePPFrontend;
import jepp.interpreter.Interpreter;

import java.io.InputStream;

public class TestJeppInterpreter {
	public static void main(String[] args) {
		String input;
		try(InputStream jeppFile = TestJeppInterpreter.class.getResourceAsStream("/test/fibonacci.jepp")) {
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

		ParseTreeNode pTree = parse.getParseTree();
		if(pTree == null) throw new Error("unknown error parsing string");

		Interpreter interpreter = new Interpreter();
		interpreter.run(pTree);
		String result = interpreter.result();
		System.out.println("--------------------\n" + result);
	}
}