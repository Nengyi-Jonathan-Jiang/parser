package jepp.interpreter;

import frontend.Token;
import frontend.lexer.Lex;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import jepp.frontend.JePPFrontend;

import java.io.InputStream;
import java.io.PrintStream;

public class Interpreter {
    private final JeppInterpreter runner;

    public Interpreter(InputStream in, PrintStream out) {
        this.runner = new JeppInterpreter(in, out);
    }

    public void run(String program) {
        Lex lex = JePPFrontend.beginLex(program);
        Parser.Parse parse = JePPFrontend.beginParse();
        Token tk;
        do {
            parse.process(tk = lex.next());
            System.out.print(tk + " ");
        } while (!tk.isEOF());

        ParseTreeNode parseTree = parse.getResult();
        if (parseTree == null) {
            throw new Error("Error parsing program: Parse tree was null");
        }

        this.runParseTree(parseTree);
    }

    public void runParseTree(ParseTreeNode parseTree) {
        this.runner.run(parseTree);
    }
}