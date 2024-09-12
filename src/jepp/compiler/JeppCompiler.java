package jepp.compiler;

import frontend.lexer.Lex;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import jepp.compiler.jeir.JeirNode;
import jepp.compiler.jeir.JeirNode.JeirStatements;
import jepp.frontend.JePPFrontend;
import jepp.jevm.Instruction;
import jepp.jevm.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JeppCompiler {
    public static Program compile(String input) {
        Lex lex = JePPFrontend.beginLex(input);
        Parser.Parse parse = JePPFrontend.beginParse();

        while (!parse.didAccept()) {
            parse.process(lex.next());
        }

        JeppCompiler compiler = new JeppCompiler(parse.getResult());

        return compiler.result();
    }

    private final List<Instruction> res = new ArrayList<>();
    private int tempVarIndex = 0;

    private JeppCompiler(ParseTreeNode parseTree) {
        JeirNode ir = createIR(parseTree);
        createResult(ir);
    }

    private Program result() {
        return new Program(res.toArray(Instruction[]::new));
    }

    private JeirNode createIR(ParseTreeNode node) {
        System.out.println("IR: Encountered " + node.getDescription().name);
        return switch (node.getDescription().name) {
            case "statements" -> new JeirStatements(
                node.children().map(this::createIR).toArray(JeirNode[]::new)
            );
            case "print-statement" -> {
                String end = switch (node.getChild(0).getValue().value) {
                    case "print" -> "";
                    case "println" -> "\n";
                    default -> throw new JeppCompileException("Unknown print statement");
                };
                if (node.getChild(1).matches("STRING-LITERAL")) {
                    String val = node.getChild(1).getValue().value;
                    yield new JeirNode.JeirPrintStatement(val.substring(1, val.length() - 1)
                        .replace("\\n", "\n")
                        .replace("\\t", "\t")
                        .replaceAll("\\\\(.)", "$1") + end);
                } else {
                    String val = node.getChild(1).getValue().value;
                    createIR(new ParseTreeNode(JePPFrontend.getSymbol("statements"),
                        new ParseTreeNode(null)
                    ));
                    yield new JeirNode.JeirPrintVarStatement(val);
                }
            }
            default -> new JeirNode.JeirNoop();
        };
    }

    public String processExpression(ParseTreeNode node) {
        String newVarName = "local$" + tempVarIndex++;

        switch (node.getDescription().name) {
            case "add-expr", "mult-expr" -> {
                ParseTreeNode arg1 = node.getChild(0),
                    arg2 = node.getChild(2);
                switch (arg1.getDescription().name) {
                    case "INT-LITERAL" -> {

                    }
                }
            }
        }

        declareLocalVariable(newVarName);
        return newVarName;
    }


    Map<String, Integer> localVars = new HashMap<>();

    private void declareLocalVariable(String name) {
        localVars.put(name, localVars.size());
    }

    private void createResult(JeirNode parseTree) {
        System.out.println("Compile: Encountered " + parseTree.getClass().getSimpleName());
        switch (parseTree) {
            case JeirStatements node -> {
                for (JeirNode child : node.children()) {
                    createResult(child);
                }
            }
            case JeirNode.JeirPrintStatement node -> res.add(new Instruction.PRINT(node.value()));
            default -> System.out.println("Unknown node");
        }
    }
}