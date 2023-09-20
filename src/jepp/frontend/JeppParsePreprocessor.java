package jepp.frontend;

import frontend.parser.ParseTreeNode;

import java.util.stream.Stream;

public class JeppParsePreprocessor {
    private static ParseTreeNode flattenStatements(ParseTreeNode parse) {
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

    private static ParseTreeNode flattenParameterList(ParseTreeNode parse) {
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

    public static ParseTreeNode process(ParseTreeNode parse) {
        ParseTreeNode res = parse.deepCopy();
        res = flattenStatements(res);
        res = flattenParameterList(res);
        return res;
    }
}
