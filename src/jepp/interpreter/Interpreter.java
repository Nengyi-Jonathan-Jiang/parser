package jepp.interpreter;

import frontend.parser.ParseTreeNode;
import jepp.frontend.JeppParsePreprocessor;

public class Interpreter {
    private final JeppInterpreter runner;

    public Interpreter() {
        this.runner = new JeppInterpreter();
    }

    public void run(ParseTreeNode parse) {
        this.runner.run(JeppParsePreprocessor.process(parse));
    }

    public String result() {
        return runner.result();
    }
}
