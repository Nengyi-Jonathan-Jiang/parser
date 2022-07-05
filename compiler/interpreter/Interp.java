package compiler.interpreter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import compiler.parsers.ParseTree;

public class Interp {
    public Deque<Value> stack;
    public Deque<Scope> scopeStack;

    StringBuilder output;

    public Interp(){
        stack = new ArrayDeque<>();
        scopeStack = new ArrayDeque<>();
        output = new StringBuilder();

        Scope scope = new Scope();

        scope.addFunc(new InterpFunction(new FunctionSignature("operator_+_", "INT", "INT"), "INT", (Object[] args) -> {
            return (Integer)args[0] + (Integer)args[1];
        }));
        scope.addFunc(new InterpFunction(new FunctionSignature("operator_-_", "INT", "INT"), "INT", (Object[] args) -> {
            return (Integer)args[0] - (Integer)args[1];
        }));
        scope.addFunc(new InterpFunction(new FunctionSignature("operator_+_", "STRING", "STRING"), "STRING", (Object[] args) -> {
            return (String)args[0] + (String)args[1];
        }));
        scope.addFunc(new InterpFunction(new FunctionSignature("operator_+_", "STRING", "INT"), "STRING", (Object[] args) -> {
            return (String)args[0] + (Integer)args[1];
        }));
        scope.addFunc(new InterpFunction(new FunctionSignature("operator_+_", "INT", "STRING"), "STRING", (Object[] args) -> {
            return (Integer)args[0] + (String)args[1];
        }));

        scopeStack.push(scope);
    }

    private Scope getCurrScope(){
        return scopeStack.peek();
    }

    public void run(ParseTree pTree){
        String nodeType = pTree.getDescription(), value = null;
        ParseTree[] children = null;
        if(pTree.isLeaf()) value = pTree.getValue().value;
        else children = pTree.getChildren();
        // System.out.println("Encountered " + nodeType);
        switch(nodeType){
            case "Statements":
                if(pTree.getChildren().length == 0) break;
                run(children[0]);
                run(children[1]);
                break;
            case "Statement":
                run(children[0]);
                break;
            case "PrintStmt":
                run(children[1]);
                Value val = stack.pop();
                System.out.println(children[0].getDescription() + " " + val);
                switch(children[0].getValue().type){
                    case "print":
                        output.append(val.toString());
                        break;
                    case "println":
                        output.append(val.toString());
                        output.append("\n");
                        break;
                }
                break;
            case "ParenthesizedExpression":
                run(children[1]);
                break;
            case "INT_LITERAL":
            case "FLOAT_LITERAL":
            case "STRING_LITERAL":
                String literal_type = nodeType.substring(0, nodeType.length() - 8);
                System.out.println(pTree.prnt());
                switch(literal_type){
                    case "INT":
                        stack.push(new Value(literal_type, Integer.parseInt(value)));
                        break;
                    case "FLOAT":
                        stack.push(new Value(literal_type, Double.parseDouble(value)));
                        break;
                    case "STRING":
                        String str = value.substring(1, value.length() - 1)
                            .replace("\\n", "\n")
                            .replace("\\t", "\t")
                            .replaceAll("\\\\(.)", "$1");
                        stack.push(new Value(literal_type, str));
                        break;
                    default:
                        stack.push(new Value(literal_type, value));
                }
                System.out.println(stack.stream().toList());
                break;
            case "AdditiveExpression":
                String operator = children[1].getDescription();
                run(children[0]);
                run(children[2]);
                Value v1 = stack.pop(), v2 = stack.pop();

                System.out.println(v1.type + " " + operator + " " + v2.type);

                InterpFunction func = getCurrScope().getFunc(new FunctionSignature("operator_" + operator + "_", v2.type, v1.type));
                stack.push(func.apply(v2, v1));

                System.out.println(stack.stream().toList());
                break;
            case "VarDeclStmt":{
                String type = children[0].getValue().value;
                String name = children[2].getValue().value;
                getCurrScope().addVar(new InterpVariable(new VariableSignature(name), type, null));
            break;}
            case "VarInitStmt":{
                String type = children[0].getValue().value;
                String name = children[2].getValue().value;
                run(children[4]);
                getCurrScope().addVar(new InterpVariable(new VariableSignature(name), type, stack.pop()));
            break;}
            default:
                break;
        }
    }

    public String result(){
        return output.toString();
    }
}

class Value{
    public String type;
    public Object value;
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

    public Scope addFunc(InterpFunction function){
        functions.put(function.getSignature(), function);
        return this;
    }

    public Scope addVar(InterpVariable variable){
        variables.put(variable.signature, variable);
        return this;
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
    public final String name, args[];

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
    public String name;
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
    public String type;
    public final Value value;
    public InterpVariable(VariableSignature signature, String type, Value value){
        this.signature = signature;
        this.value = value;
        this.type = type;
    }
}

class InterpFunction{
    private Function<Object[], Object> function;
    private FunctionSignature signature;
    private String retType;
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