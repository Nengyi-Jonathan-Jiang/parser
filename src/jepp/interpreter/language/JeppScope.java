package jepp.interpreter.language;

import jepp.interpreter.JeppInterpreterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class JeppScope {
    private final Map<String, JeppValue> variables;
    private final Map<String, Map<JeppMethodSignature, JeppMethod>> methods;
    private final Map<String, JeppType> types;
    private final JeppScope parentScope;

    public JeppScope(JeppScope parentScope) {
        variables = new HashMap<>();
        methods = new HashMap<>();
        types = new HashMap<>();

        this.parentScope = parentScope;
    }

    private boolean hasOwnVariable(String name) {
        return variables.containsKey(name);
    }
    private boolean hasVariable(String name) {
        return hasOwnVariable(name) || parentScope != null && parentScope.hasType(name);
    }
    private boolean hasOwnMethod(String name, JeppMethodSignature signature) {
        return methods.containsKey(name) && methods.get(name).containsKey(signature);
    }
    private boolean hasMethod(String name, JeppMethodSignature signature) {
        return hasOwnMethod(name, signature) || parentScope != null && parentScope.hasMethod(name, signature);
    }
    private boolean hasOwnType(String name) {
        return types.containsKey(name);
    }
    private boolean hasType(String name) {
        return hasOwnType(name) || parentScope != null && parentScope.hasType(name);
    }

    public JeppValue getVariable(String name){
        if (hasVariable(name)) return variables.get(name);
        if (parentScope != null) return parentScope.getVariable(name);
        throw new JeppInterpreterException("Could not find variable " + name);
    }

    public @Nullable JeppMethod getMethod(String name, JeppType... signature){
        return getMethod(name, new JeppMethodSignature(signature));
    }

    public @Nullable JeppMethod getMethod(String name, JeppMethodSignature signature){
        if (hasOwnMethod(name, signature)) return methods.get(name).get(signature);
        if (parentScope != null) return parentScope.getMethod(name, signature);
        return null;
    }

    private @NotNull Map<JeppMethodSignature, JeppMethod> allMethodsForName(String name){
        Map<JeppMethodSignature, JeppMethod> res = ownMethodsForName(name);
        if(parentScope != null) parentScope.allMethodsForName(name).forEach(res::putIfAbsent);
        return res;
    }

    private Map<JeppMethodSignature, JeppMethod> ownMethodsForName(String name) {
        return methods.get(name);
    }

    public JeppMethod getMethodForArgs(String name, JeppType... types) {
        Map<JeppMethodSignature, JeppMethod> methods = allMethodsForName(name);
        if(methods.isEmpty()) return null;
        if(methods.containsKey(new JeppMethodSignature(types))) return methods.get(new JeppMethodSignature(types));

        // TODO: (much later) Implement stuff

        return null;
    }

    public JeppType getType(String name){
        return hasType(name) ? types.get(name) : parentScope != null ? parentScope.getType(name) : null;
    }

    public void setVariable(String name, JeppValue value) {
        if (!hasOwnVariable(name) && hasVariable(name)) {
            parentScope.setVariable(name, value);
        } else {
            variables.put(name, value);
        }
    }

    public void registerMethod(JeppMethod method) {
        if(hasOwnMethod(method.name(), method.signature())) throw new JeppInterpreterException("Method " + method.name() + "(" + method.signature() + ") already exists in scope");
        else {
            if(!methods.containsKey(method.name())) methods.put(method.name(), new TreeMap<>());
            methods.get(method.name()).put(method.signature(), method);
        }
    }

    public void defineType(JeppType type) {
        if(hasOwnType(type.fullName())) throw new JeppInterpreterException("Type " + type.fullName() + " already exists in scope");
        else types.put(type.fullName(), type);
    }
}
