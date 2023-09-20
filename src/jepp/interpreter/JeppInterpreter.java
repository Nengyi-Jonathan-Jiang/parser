package jepp.interpreter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;

import frontend.parser.ParseTreeNode;
import jepp.language.*;
import jepp.language.builtin.JeppBaseScope;
import jepp.language.builtin.types.PrimitiveJeppType;
import jepp.language.builtin.types.PrimitiveJeppValue;

public class JeppInterpreter {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintWriter out = new PrintWriter(new BufferedOutputStream(output));
    private final Scanner scan;

    private final Stack<JeppScope> scopeStack = new Stack<>();

    public JeppInterpreter() {
        scan = new Scanner(System.in);
        scopeStack.push(new JeppBaseScope());
    }

    public void run(ParseTreeNode pTree) {
        evaluate(pTree);
    }

    public JeppValue evaluate(ParseTreeNode node) {
        switch (node.getDescription().name) {
            case "return-statement" -> {
                return evaluate(node.getChild(1));
            }
            case "expression-statement" -> evaluate(node.getChild(0));
            case "statements" -> {
                for (ParseTreeNode statement : node.getChildren()) {
                    JeppValue value = evaluate(statement);
                    if (value.getType() != PrimitiveJeppType.JVoidT) {
                        return value;
                    }
                }
            }
            case "call-expr" -> {
                String methodName = node.getChild(0).getValue().value;

                List<JeppValue> arguments = new ArrayList<>();
                ParseTreeNode argsNode = node.getChild(2);
                while (argsNode.getDescription().name.equals("call-arguments")) {
                    arguments.add(evaluate(argsNode.getChild(0)));
                    if (argsNode.getChildren().length == 3) argsNode = argsNode.getChild(2);
                }
                if (!argsNode.getDescription().toString().equals(")")) arguments.add(evaluate(argsNode));

                List<JeppType> argTypes = arguments.stream().map(JeppValue::getType).toList();

                JeppMethod m = currentScope().getMethod(methodName, argTypes.toArray(JeppType[]::new));
                if (m == null) throw new JeppUnknownMethodException(methodName, argTypes);
                return m.apply(this, arguments.toArray(JeppValue[]::new));
            }

            case "IDENTIFIER" -> {
                return currentScope().getVariable(node.getValue().value);
            }

            case "add-expr", "mult-expr" -> {
                JeppValue val1 = evaluate(node.getChild(0));
                JeppValue val2 = evaluate(node.getChild(2));
                String op = node.getChild(1).getValue().value;

                JeppMethod m = currentScope().getMethod("operator" + op, val1.getType(), val2.getType());
                if (m == null) throw new JeppUnknownOperatorException(op, val1, val2);
                return m.apply(this, val1, val2);
            }
            case "assignment-expr" -> {
                String variableName = node.getChild(0).getValue().value;
                JeppValue val = evaluate(node.getChild(2));
                String op = node.getChild(1).getValue().value;

                if (!op.equals("=")) {
                    // compound assignment operator
                    JeppValue prevVal = currentScope().getVariable(variableName);
                    op = op.substring(0, op.length() - 1);

                    JeppMethod m = currentScope().getMethod("operator" + op, prevVal.getType(), val.getType());
                    if (m == null) throw new JeppUnknownOperatorException(op, prevVal, val);
                    val = m.apply(this, prevVal, val);
                }

                currentScope().setVariable(variableName, val);

                System.out.println(variableName + " <- " + val);

                return val;
            }

            case "primary-expr" -> {
                return evaluate(node.getChild(1));
            }
            case "INT-LITERAL" -> {
                String tok_val = node.getValue().value;
                return new PrimitiveJeppValue.JInteger(Integer.parseInt(tok_val));
            }
            case "FLOAT-LITERAL" -> {
                String tok_val = node.getValue().value;
                return new PrimitiveJeppValue.JDouble(Double.parseDouble(tok_val));
            }
            case "print-statement" -> {
                Object value;
                if (node.getChild(1).getDescription().name.equals("STRING-LITERAL")) {
                    String val = node.getChild(1).getValue().value;
                    value = val.substring(1, val.length() - 1);
                } else value = evaluate(node.getChild(1));

                switch(node.getChild(0).getValue().value) {
                    case "print" -> out.print(value);
                    case "println" -> out.println(value);
                }
            }
            default -> System.out.println("Got " + node.getDescription());
        }
        return PrimitiveJeppValue.Void;
    }

    public JeppScope currentScope() {
        return scopeStack.peek();
    }

    public JeppScope pushNewScope() {
        JeppScope s = new JeppScope(scopeStack.peek());
        scopeStack.push(s);
        return s;
    }

    public void popScope(JeppScope s) {
        if (currentScope() == s) scopeStack.pop();
        else throw new JeppInterpreterPanic("Bad scope unwinding");
    }

    public String result() {
        out.flush();
        return output.toString();
    }
}