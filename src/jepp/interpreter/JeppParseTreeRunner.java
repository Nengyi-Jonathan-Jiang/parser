package jepp.interpreter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;

import compiler.parser.ParseTreeNode;
import jepp.language.*;
import jepp.language.builtin.JeppBaseScope;
import jepp.language.builtin.types.PrimitiveJeppValue;

public class JeppParseTreeRunner {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintWriter out = new PrintWriter(new BufferedOutputStream(output));
    private final Scanner scan;

    private final Stack<JeppScope> scopeStack = new Stack<>();

    public JeppParseTreeRunner() {
        scan = new Scanner(System.in);
        scopeStack.push(new JeppBaseScope());
    }

    public void run(ParseTreeNode pTree) {
        evaluate(pTree);
    }

    public JeppValue evaluate(ParseTreeNode node) {
        switch (node.getDescription().name) {
            case "statements" -> node.children().forEach(this::evaluate);
            case "call_expr" -> {
                String methodName = node.getChild(0).getValue().value;

                List<JeppValue> arguments = new ArrayList<>();
                ParseTreeNode argsNode = node.getChild(2);
                while (argsNode.getDescription().name.equals("call-arguments")) {
                    arguments.add(evaluate(argsNode.getChild(0)));
                    if (argsNode.getChildren().length == 3) argsNode = argsNode.getChild(2);
                }
                if (!argsNode.getDescription().toString().equals(")")) arguments.add(evaluate(argsNode));

                List<JeppType> argTypes = arguments.stream().map(JeppValue::getType).toList();

                JeppMethod m = scopeStack.peek().getMethod(methodName, argTypes.toArray(JeppType[]::new));
                if (m == null) throw new JeppUnknownMethodException(methodName, argTypes);
                return m.apply(scopeStack.peek(), arguments.toArray(JeppValue[]::new));
            }

            case "add_expr", "mult_expr" -> {
                JeppValue val1 = evaluate(node.getChild(0));
                JeppValue val2 = evaluate(node.getChild(2));
                String op = node.getChild(1).getValue().value;

                JeppMethod m = scopeStack.peek().getMethod("operator" + op, val1.getType(), val2.getType());
                if (m == null) throw new JeppUnknownOperatorException(op, val1, val2);
                return m.apply(scopeStack.peek(), val1, val2);
            }

            case "primary_expr" -> {
                return evaluate(node.getChild(1));
            }
            case "STRING_LITERAL" -> {
                String tok_val = node.getValue().value;
                return new PrimitiveJeppValue.JString(tok_val.substring(1, tok_val.length() - 1));
            }
            case "INT_LITERAL" -> {
                String tok_val = node.getValue().value;
                return new PrimitiveJeppValue.JInteger(Integer.parseInt(tok_val));
            }
            case "FLOAT_LITERAL" -> {
                String tok_val = node.getValue().value;
                return new PrimitiveJeppValue.JDouble(Double.parseDouble(tok_val));
            }
            case "print_statement" -> {
                out.println(evaluate(node.getChild(1)));
            }
            default -> System.out.println("Got " + node.getDescription());
        }
        return PrimitiveJeppValue.Void;
    }

    public String result() {
        out.flush();
        return output.toString();
    }
}