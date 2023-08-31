package compiler.backend.interpreter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import compiler.frontend.parser.ParseTree;

public class Interp {
    public final Deque<Value> stack;
    public final Deque<Scope> scopeStack;
    private final Scanner scan;

    final StringBuilder output;

    public Interp(){
        stack = new ArrayDeque<>();
        scopeStack = new ArrayDeque<>();
        output = new StringBuilder();

        Scope scope = new Scope();

        scope.addFunc(new InterpFunction(new FunctionSignature("operator_+_", "INT", "INT"), "INT", (Object[] args) -> (Integer)args[0] + (Integer)args[1]));
        scope.addFunc(new InterpFunction(new FunctionSignature("operator_-_", "INT", "INT"), "INT", (Object[] args) -> (Integer)args[0] - (Integer)args[1]));
        scope.addFunc(new InterpFunction(new FunctionSignature("operator_+_", "STRING", "STRING"), "STRING", (Object[] args) -> args[0] + (String)args[1]));
        scope.addFunc(new InterpFunction(new FunctionSignature("operator_+_", "STRING", "INT"), "STRING", (Object[] args) -> (String)args[0] + args[1]));
        scope.addFunc(new InterpFunction(new FunctionSignature("operator_+_", "INT", "STRING"), "STRING", (Object[] args) -> args[0] + (String)args[1]));

        scopeStack.push(scope);

        scan = new Scanner(System.in);
    }

    public void close(){
        scan.close();
    }

    private Scope getCurrScope(){
        return scopeStack.peek();
    }

    public void run(ParseTree pTree){

        if(0 == 0) return;

        String nodeType = pTree.getDescription().string, value = null;
        ParseTree[] children = null;
        if(pTree.isLeaf()) value = pTree.getValue().value;
        else children = pTree.getChildren();
        // System.out.println("Encountered " + nodeType);
        switch (nodeType) {
            case "Statements" -> {
                if (pTree.getChildren().length == 0) break;
                run(children[0]);
                run(children[1]);
            }
            case "Statement" -> run(children[0]);
            case "PrintStmt" -> {
                run(children[1]);
                Value val = stack.pop();
                // System.out.println(children[0].getDescription() + " " + val);
                output.append(val.toString());
                if (children[0].getDescription().toString().equals("println")) {
                    output.append("\n");
                }
            }
            case "ParenthesizedExpression" -> run(children[1]);
            case "INT_LITERAL", "FLOAT_LITERAL", "STRING_LITERAL" -> {
                String literal_type = nodeType.substring(0, nodeType.length() - 8);

                switch (literal_type) {
                    case "INT" -> stack.push(new Value(literal_type, Integer.parseInt(value)));
                    case "FLOAT" -> stack.push(new Value(literal_type, Double.parseDouble(value)));
                    case "STRING" -> {
                        String str = value.substring(1, value.length() - 1)
                                .replace("\\n", "\n")
                                .replace("\\t", "\t")
                                .replaceAll("\\\\(.)", "$1");
                        stack.push(new Value(literal_type, str));
                    }
                    default -> stack.push(new Value(literal_type, value));
                }
            }
            // System.out.println(stack.stream().collect(Collectors.toList()));
            case "AdditiveExpression" -> {
                String operator = children[1].getDescription().string;
                run(children[0]);
                run(children[2]);
                Value v1 = stack.pop(), v2 = stack.pop();

                // System.out.println(v1.type + " " + operator + " " + v2.type);

                InterpFunction func = getCurrScope().getFunc(new FunctionSignature("operator_" + operator + "_", v2.type, v1.type));
                stack.push(func.apply(v2, v1));
            }

            // System.out.println(stack.stream().collect(Collectors.toList()));
            case "VarDeclStmt" -> {
                String type = children[0].getValue().value;
                String name = children[2].getValue().value;
                getCurrScope().addVar(new InterpVariable(new VariableSignature(name), type, null));
            }
            case "VarInitStmt" -> {
                String type = children[0].getValue().value;
                String name = children[2].getValue().value;
                run(children[4]);
                getCurrScope().addVar(new InterpVariable(new VariableSignature(name), type, stack.pop()));
            }
            default -> {
            }
        }
    }

    public String result(){
        return output.toString();
    }
}

class Value{
    public final String type;
    public final Object value;
    public Value(String type, Object value){
        this.type = type;
        this.value = value;
    }
    public String toString(){
        return this.value.toString();
    }
}


class Scope{
    private final Scope parent;
    private final Map<FunctionSignature, InterpFunction> functions;
    private final Map<VariableSignature, InterpVariable> variables;
    // private Map<String, InterpClass> classes;
    public Scope(){
        this(null);
    }
    public Scope(Scope parent){
        this.parent = parent;
        this.functions = new TreeMap<>();
        this.variables = new TreeMap<>();
    }

    public void addFunc(InterpFunction function){
        functions.put(function.getSignature(), function);
    }

    public void addVar(InterpVariable variable){
        variables.put(variable.signature, variable);
    }

    public InterpVariable getVariable(VariableSignature name){
        InterpVariable res = variables.get(name);
        if(res != null) return res;
        if(parent != null) res = parent.getVariable(name);
        if(res != null) return res;
        throw new Error("Could not access variable " + name);
    }

    public InterpFunction getFunc(FunctionSignature signature){
        InterpFunction res = functions.get(signature);
        if(res != null) return res;
        if(parent != null) res = parent.getFunc(signature);
        if(res != null) return res;
        throw new Error("Could not access function " + signature);
    }
}

class FunctionSignature implements Comparable<FunctionSignature>{
    public final String name;
    public final String[] args;

    public FunctionSignature(String name, String... args){
        this.args = args;
        this.name = name;
    }

    public boolean equals(FunctionSignature other){
        return Arrays.deepEquals(args, other.args) && name.equals(other.name);
    }

    public int compareTo(FunctionSignature other){
        int nameCompare = name.compareTo(other.name);
        return nameCompare == 0 ? Arrays.compare(args, other.args) : nameCompare;
    }

    public String toString(){
        return name + "(" + Arrays.stream(args).collect(Collectors.joining(", ")) + ")";
    }
}

class VariableSignature{
    public final String name;
    public VariableSignature(String name){
        this.name = name;
    }
    public int compareTo(VariableSignature other){
        return name.compareTo(other.name);
    }
    public boolean equals(VariableSignature other){
        return name.equals(other.name);
    }
    public String toString(){
        return name;
    }
}

class InterpVariable{
    public final VariableSignature signature;
    public final String type;
    public final Value value;
    public InterpVariable(VariableSignature signature, String type, Value value){
        this.signature = signature;
        this.value = value;
        this.type = type;
    }
}

class InterpFunction{
    private final Function<Object[], Object> function;
    private final FunctionSignature signature;
    private final String retType;
    public InterpFunction(FunctionSignature signature, String retType, Function<Object[], Object> function){
        this.signature = signature;
        this.retType = retType;
        this.function = function;
    }

    public String getName(){
        return signature.name;
    }
    public FunctionSignature getSignature(){
        return signature;
    }
    public String getRetType(){
        return retType;
    }
    public Value apply(Value... args){
        Object res = function.apply(Arrays.stream(args).map(i->i.value).toArray());
        return new Value(retType, res);
    }
    public void addToMap(Map<FunctionSignature, InterpFunction> m){
        m.put(signature, this);
    }
}