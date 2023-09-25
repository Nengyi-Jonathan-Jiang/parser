package jepp.interpreter;

import frontend.parser.ParseTreeNode;
import jepp.frontend.JeppParsePreprocessor;

import java.io.InputStream;
import java.io.PrintStream;

public class Interpreter {
    private final JeppInterpreter runner;

    public Interpreter(InputStream in, PrintStream out) {
        this.runner = new JeppInterpreter(in, out);
    }

    public void run(ParseTreeNode parse) {
        this.runner.run(JeppParsePreprocessor.process(parse));
    }
}