package jepp.frontend;

import frontend.parser.ParseTreeNode;

import java.util.stream.Stream;

public class JeppParsePreprocessor {
    private static ParseTreeNode flatten(ParseTreeNode parse) {
        return switch (parse.getDescription().name) {
            case "statements" -> {
                if (parse.getChildren().length == 0) yield flattenChildren(parse);
                var statement = flatten(parse.getChild(0));
                var rest = parse.getChild(1);
                ParseTreeNode[] newChildren = Stream.concat(Stream.of(statement), flatten(rest).children()).toArray(ParseTreeNode[]::new);
                yield new ParseTreeNode(parse.getDescription(), newChildren);
            }
            case "argument" ->
                    new ParseTreeNode(JePPFrontend.symbolTable.get("argument-list"), flattenChildren(parse));
            case "parameter" -> new ParseTreeNode(JePPFrontend.symbolTable.get("parameters"), flattenChildren(parse));
            case "call-expr" -> {
                if (parse.getChildren().length != 3) {
                    var x = parse.getChildren()[2];
                    if (!x.matches("argument-list")) {
                        parse.getChildren()[2] = flatten(new ParseTreeNode(JePPFrontend.symbolTable.get("argument-list"), x));
                    }
                }
                yield flattenChildren(parse);
            }
            case "argument-list" -> {
                if (parse.getChildren().length == 1) yield flattenChildren(parse);
                var param = flattenChildren(parse.getChild(0));
                var rest = flatten(parse.getChild(2));
                if (rest.matches("argument-list")) {
                    ParseTreeNode[] newChildren = Stream.concat(Stream.of(param), rest.children()).toArray(ParseTreeNode[]::new);
                    yield new ParseTreeNode(JePPFrontend.symbolTable.get("argument-list"), newChildren);
                } else {
                    yield new ParseTreeNode(JePPFrontend.symbolTable.get("argument-list"), param, rest);
                }
            }
            case "parameters" -> {
                if (parse.getChildren().length == 1) yield flattenChildren(parse);
                var param = flattenChildren(parse.getChild(0));
                var rest = flatten(parse.getChild(2));
                ParseTreeNode[] newChildren = Stream.concat(Stream.of(param), rest.children()).toArray(ParseTreeNode[]::new);
                yield new ParseTreeNode(JePPFrontend.symbolTable.get("parameters"), newChildren);
            }

            default -> flattenChildren(parse);
        };
    }

    private static ParseTreeNode flattenChildren(ParseTreeNode parse) {
        if (parse.isLeaf()) return parse;
        for (int i = 0; i < parse.getChildren().length; i++)
            parse.getChildren()[i] = flatten(parse.getChildren()[i]);
        return parse;
    }

    public static ParseTreeNode process(ParseTreeNode parse) {
        ParseTreeNode res = parse.deepCopy();
        res = flatten(res);
        return res;
    }
}
