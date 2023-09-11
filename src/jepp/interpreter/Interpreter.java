package jepp.interpreter;

import compiler.parser.ParseTreeNode;

import java.util.stream.Stream;

public class Interpreter {
    private final JeppInterpreter runner;

    public Interpreter() {
        this.runner = new JeppInterpreter();
    }

    public ParseTreeNode flattenStatements(ParseTreeNode parse) {
        if (parse.isLeaf()) return parse;
        if (parse.getDescription().name.equals("statements")) {
            if (parse.getChildren().length == 0) return parse;
            var statement = parse.getChild(0);
            var rest = parse.getChild(1);
            ParseTreeNode[] newChildren = Stream.concat(Stream.of(statement), flattenStatements(rest).children()).toArray(ParseTreeNode[]::new);
            return new ParseTreeNode(parse.getDescription(), newChildren);
        }
        for (int i = 0; i < parse.getChildren().length; i++)
            parse.getChildren()[i] = flattenStatements(parse.getChildren()[i]);
        return parse;
    }

    public ParseTreeNode flattenParameterList(ParseTreeNode parse) {
        if (parse.isLeaf()) return parse;
        if (parse.getDescription().name.equals("parameters")) {
            if (parse.getChildren().length == 1) return parse;
            var param = parse.getChild(0);
            var rest = parse.getChild(2);
            ParseTreeNode[] newChildren = Stream.concat(Stream.of(param), flattenParameterList(rest).children()).toArray(ParseTreeNode[]::new);
            return new ParseTreeNode(parse.getDescription(), newChildren);
        }
        for (int i = 0; i < parse.getChildren().length; i++)
            parse.getChildren()[i] = flattenParameterList(parse.getChildren()[i]);
        return parse;
    }

    public void run(ParseTreeNode parse) {
        ParseTreeNode transformed = parse.deepCopy();
        transformed = flattenStatements(transformed);
        transformed = flattenParameterList(transformed);
        System.out.println(transformed);
        this.runner.run(parse);
    }

    public String result() {
        return runner.result();
    }
}
