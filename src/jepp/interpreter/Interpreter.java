package jepp.interpreter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;

import compiler.parser.ParseTreeNode;
import jepp.language.*;

public class Interpreter {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintWriter out = new PrintWriter(new BufferedOutputStream(output));
    private final Scanner scan;

    private final Stack<JeppScope> scopeStack = new Stack<>();

    public Interpreter(){
        scan = new Scanner(System.in);
        scopeStack.push(new JeppBaseScope());
    }

    public void run(ParseTreeNode pTree){
        evaluate(pTree);
    }

    public JeppValue evaluate(ParseTreeNode node) {
        switch (node.getDescription().name) {
            case "statements" -> {
                if(node.getChildren().length == 0) return null;
                for (ParseTreeNode child : node.getChildren()) {
                    evaluate(child);
                }
            }
            case "add_expr" -> {
                JeppValue val1 = evaluate(node.getChildren()[0]);
                JeppValue val2 = evaluate(node.getChildren()[2]);
                String op = node.getChildren()[1].getValue().value;

                if(val1.getType() == PrimitiveJeppType.JStringT || val2.getType() == PrimitiveJeppType.JStringT) {
                    if(!op.equals("+")) throw new JeppInterpreterException("Cannot apply operator " + op + " to strings");
                    return new PrimitiveJeppValue.JString("" + val1 + val2);
                }

                switch (op) {
                    case "+", "-" -> {
                        if(val1.getType() == PrimitiveJeppType.JDoubleT || val2.getType() == PrimitiveJeppType.JDoubleT) {
                            double d1 = val1.getType() == PrimitiveJeppType.JDoubleT 
                                    ? ((PrimitiveJeppValue.JDouble)val1).value
                                    : ((PrimitiveJeppValue.JInteger)val1).value;
                            double d2 = val2.getType() == PrimitiveJeppType.JDoubleT
                                    ? ((PrimitiveJeppValue.JDouble)val2).value
                                    : ((PrimitiveJeppValue.JInteger)val2).value;
                            return new PrimitiveJeppValue.JDouble(switch(op){
                                case "+" -> d1 + d2;
                                case "-" -> d1 - d2;
                                default -> throw new JeppInterpreterPanic();
                            });
                        }
                        if(val1.getType() == PrimitiveJeppType.JIntegerT && val2.getType() == PrimitiveJeppType.JIntegerT) {
                            int i1 = ((PrimitiveJeppValue.JInteger)val1).value;
                            int i2 = ((PrimitiveJeppValue.JInteger)val2).value;
                            return new PrimitiveJeppValue.JInteger(switch(op){
                                case "+" -> i1 + i2;
                                case "-" -> i1 - i2;
                                default -> throw new JeppInterpreterPanic();
                            });
                        }
                    }
                }

                throw new JeppInterpreterException("Unknown operator " + op + " on types " + val1.getType().fullName() + " and " + val2.getType().fullName());
            }
            case "mult_expr" -> {
                JeppValue val1 = evaluate(node.getChildren()[0]);
                JeppValue val2 = evaluate(node.getChildren()[2]);
                String op = node.getChildren()[1].getValue().value;

                switch (op) {
                    case "*", "/", "%" -> {
                        if(val1.getType() == PrimitiveJeppType.JDoubleT || val2.getType() == PrimitiveJeppType.JDoubleT) {
                            double d1 = val1.getType() == PrimitiveJeppType.JDoubleT
                                    ? ((PrimitiveJeppValue.JDouble)val1).value
                                    : ((PrimitiveJeppValue.JInteger)val1).value;
                            double d2 = val2.getType() == PrimitiveJeppType.JDoubleT
                                    ? ((PrimitiveJeppValue.JDouble)val2).value
                                    : ((PrimitiveJeppValue.JInteger)val2).value;
                            return new PrimitiveJeppValue.JDouble(switch(op){
                                case "*" -> d1 * d2;
                                case "/" -> d1 / d2;
                                case "%" -> (d1 % d2 + d2) % d2;
                                default -> throw new JeppInterpreterPanic();
                            });
                        }
                        if(val1.getType() == PrimitiveJeppType.JIntegerT && val2.getType() == PrimitiveJeppType.JIntegerT) {
                            int i1 = ((PrimitiveJeppValue.JInteger)val1).value;
                            int i2 = ((PrimitiveJeppValue.JInteger)val2).value;
                            return new PrimitiveJeppValue.JInteger(switch(op){
                                case "*" -> i1 * i2;
                                case "/" -> i1 / i2;
                                case "%" -> (i1 % i2 + i2) % i2;
                                default -> throw new JeppInterpreterPanic();
                            });
                        }
                    }
                }

                throw new JeppInterpreterException("Unknown operator " + op + " on types " + val1.getType().fullName() + " and " + val2.getType().fullName());
            }
            case "primary_expr" -> {
                return evaluate(node.getChildren()[1]);
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
                out.println(evaluate(node.getChildren()[1]));
            }
            default -> System.out.println("Got " + node.getDescription());
        }
        return PrimitiveJeppValue.Void;
    }

    public String result(){
        out.flush();
        return output.toString();
    }
}