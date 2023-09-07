package jepp.language.builtin.methods;

import jepp.language.*;

public abstract class BuiltinJeppMethod implements JeppMethod {
    private final String name;
    private final JeppType _returnType;
    JeppMethodSignature signature;

    public BuiltinJeppMethod(String name, JeppMethodSignature signature, JeppType returnType) {
        this.name = name;
        this.signature = signature;
        this._returnType = returnType;
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final JeppMethodSignature signature() {
        return signature;
    }

    @Override
    public final JeppType returnType() {
        return _returnType;
    }
}
