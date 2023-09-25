package jepp.interpreter;

import java.io.*;
import java.util.*;

import frontend.parser.ParseTreeNode;
import jepp.frontend.JePPFrontend;
import jepp.interpreter.language.*;
import jepp.interpreter.language.builtin.JeppBaseScope;
import org.jetbrains.annotations.NotNull;

import static jepp.interpreter.language.builtin.types.PrimitiveJeppType.*;
import static jepp.interpreter.language.builtin.types.PrimitiveJeppValue.*;
import static jepp.interpreter.language.builtin.types.PrimitiveJeppValue.JCompare.CompareCondition.fromString;

public class JeppInterpreter {
    private final Scanner scan;

    private final Stack<JeppScope> scopeStack = new Stack<>();
    private final PrintStream out;

    public JeppInterpreter(InputStream in, PrintStream out) {
        this.out = out;
        scan = new Scanner(in);

        scopeStack.push(new JeppBaseScope());
    }

    public void run(ParseTreeNode pTree) {
        System.out.println(pTree);
        evaluate(pTree);
    }

    public @NotNull JeppValue evaluate(ParseTreeNode node) {
        switch (node.getDescription().name) {
            case "function-definition" -> {
                String name = node.getChild(1).getValue().value;

                ParseTreeNode code;
                if(node.getChild(4).getDescription().equals("return")) {
                    code = new ParseTreeNode(
                            JePPFrontend.symbolTable.get("return-statement"), node.getChild(4), node.getChild(6)
                    );
                }
                else code = node.getChild(5);

                ParseTreeNode parameters = node.getChild(2);

                List<String> argNames = new ArrayList<>();
                List<JeppType> argTypes = new ArrayList<>();

                for(ParseTreeNode parameter :
                    parameters.getChild(1).getChildren().length == 2 ? new ParseTreeNode[]{} : parameters.getChild(1).getChildren()
                ) {
                    String argType, argName;
                    if(parameter.getChildren().length == 3) {
                        argType = parameter.getChild(1).getValue().value;
                        argName = parameter.getChild(2).getValue().value;
                    }
                    else {
                        argType = parameter.getChild(0).getValue().value;
                        argName = parameter.getChild(1).getValue().value;
                    }

                    argTypes.add(switch (argType) {
                        case "int" -> JIntegerT;
                        case "float" -> JFloatT;
                        default -> throw new IllegalArgumentException("Wrong type");
                    });
                    argNames.add(argName);
                }

                JeppMethodSignature signature = new JeppMethodSignature(argTypes.toArray(JeppType[]::new));
                JeppMethodPrototype prototype = new JeppMethodPrototype(signature, argNames.toArray(String[]::new));

                System.out.println("Registered " + prototype);

                currentScope().registerMethod(new UserJeppMethod(name, prototype, code));
            }
            case "return-statement" -> {
                return evaluate(node.getChild(1));
            }
            case "expression-statement" -> evaluate(node.getChild(0));
            case "statements" -> {
                for (ParseTreeNode statement : node.getChildren()) {
                    JeppValue value = evaluate(statement);
                    if (value != Void) return value;
                }
            }
            case "if-clause" -> {
                if(evaluate(node.getChild(2)).isTruthy()) {
                    return evaluate(node.getChild(4));
                }
            }
            case "if-else-statement" -> {
                if(evaluate(node.getChild(0).getChild(2)).isTruthy()) {
                    return evaluate(node.getChild(0).getChild(4));
                }
                else {
                    return evaluate(node.getChild(1).getChild(1));
                }
            }
            case "block-statement" -> {
                return evaluate(node.getChild(1));
            }
            case "call-expr" -> {
                String methodName = node.getChild(0).getValue().value;

                List<JeppValue> arguments = node.getChild(2).children().map(this::evaluate).toList();
                List<JeppType> argTypes = arguments.stream().map(JeppValue::getType).toList();

                JeppMethod m = currentScope().getMethod(methodName, argTypes.toArray(JeppType[]::new));
                if (m == null) throw new JeppUnknownMethodException(methodName, argTypes);
                return m.apply(this, arguments.toArray(JeppValue[]::new));
            }

            case "IDENTIFIER" -> {
                return currentScope().getVariable(node.getValue().value);
            }

            case "input-expr" -> {
                return switch(node.getChild(1).getValue().value) {
                    case "int" -> new JInteger(scan.nextInt());
                    case "float" ->  new JFloat(scan.nextFloat());
                    default -> throw new UnsupportedOperationException("Unknown input type");
                };
            }

            case "add-expr", "mult-expr" -> {
                JeppValue val1 = evaluate(node.getChild(0));
                JeppValue val2 = evaluate(node.getChild(2));
                String op = node.getChild(1).getValue().value;

                JeppMethod m = currentScope().getMethod("operator" + op, val1.getType(), val2.getType());
                if (m == null) throw new JeppUnknownOperatorException(op, val1, val2);
                return m.apply(this, val1, val2);
            }
            case "compare-expr" -> {
                JeppValue val1 = evaluate(node.getChild(0));
                JeppValue val2 = evaluate(node.getChild(2));
                String op = node.getChild(1).getValue().value;

                JeppMethod m = currentScope().getMethod("operator<=>", val1.getType(), val2.getType());
                if (m == null) throw new JeppUnknownOperatorException("<=>", val1, val2);
                JeppValue res = m.apply(this, val1, val2);
                if (res.getType() != JCompareT) throw new JeppInterpreterException("Comparison must return JCompare");
                return new JBoolean(((JCompare)res).matches(fromString(op)));
            }
            case "decl-statement" -> {
                String variableName = node.getChild(1).getValue().value;
                JeppValue val = evaluate(node.getChild(3));

                currentScope().setVariable(variableName, val);

                return Void;
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

                return val;
            }

            case "primary-expr" -> {
                return evaluate(node.getChild(1));
            }
            case "INT-LITERAL" -> {
                String tok_val = node.getValue().value;
                return new JInteger(Integer.parseInt(tok_val));
            }
            case "FLOAT-LITERAL" -> {
                String tok_val = node.getValue().value;
                return new JFloat(Float.parseFloat(tok_val));
            }
            case "print-statement" -> {
                String val;
                if (node.getChild(1).getDescription().equals("STRING-LITERAL")) {
                    val = node.getChild(1).getValue().value;
                    val = val.substring(1, val.length() - 1)
                             .replace("\\n", "\n")
                             .replace("\\t", "\t")
                             .replaceAll("\\\\(.)", "$1");
                } else {
                    val = evaluate(node.getChild(1)).toString();
                }

                switch(node.getChild(0).getValue().value) {
                    case "print" -> out.print(val);
                    case "println" -> out.println(val);
                }
            }
            default -> System.out.println("Got unknown node " + node.getDescription());
        }
        return Void;
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
}