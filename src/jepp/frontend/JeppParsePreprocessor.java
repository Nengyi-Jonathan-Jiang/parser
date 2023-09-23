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
            case "call-argument" -> {
                System.out.println("wrap call argument");
                yield new ParseTreeNode(JePPFrontend.symbolTable.get("call-arguments"), flattenChildren(parse));
            }
            case "parameter" -> {
                System.out.println("wrap parameter");
                yield new ParseTreeNode(JePPFrontend.symbolTable.get("parameters"), flattenChildren(parse));
            }
            case "call-expr" -> {
                if (parse.getChildren().length == 3) {
                    yield flattenChildren(parse);
                } else {
                    var x = parse.getChildren()[2];
                    if (!x.getDescription().equals("call-arguments")) {
                        System.out.println("wrap call args");
                        parse.getChildren()[2] = flatten(new ParseTreeNode(JePPFrontend.symbolTable.get("call-arguments"), x));
                    }
                    yield flattenChildren(parse);
                }
            }
            case "call-arguments" -> {
                System.out.println("flatten call args " + parse);

                if (parse.getChildren().length == 1) yield flattenChildren(parse);
                var param = flattenChildren(parse.getChild(0));
                var rest = flatten(parse.getChild(2));
                if (rest.getDescription().equals("call-arguments")) {
                    ParseTreeNode[] newChildren = Stream.concat(Stream.of(param),
                            rest.children()
                    ).toArray(ParseTreeNode[]::new);
                    yield new ParseTreeNode(JePPFrontend.symbolTable.get("call-arguments"), newChildren);
                } else {
                    yield new ParseTreeNode(JePPFrontend.symbolTable.get("call-arguments"), param, rest);
                }
            }
            case "parameters" -> {
                System.out.println("flatten parameters");

                if (parse.getChildren().length == 1) yield flattenChildren(parse);
                var param = flattenChildren(parse.getChild(0));
                var rest = flatten(parse.getChild(2));
                ParseTreeNode[] newChildren = Stream.concat(Stream.of(param),
                        rest.children()
                ).toArray(ParseTreeNode[]::new);
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
