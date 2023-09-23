package jepp.language.builtin.methods;

import jepp.interpreter.JeppInterpreter;
import jepp.language.JeppMethodSignature;
import jepp.language.JeppType;
import jepp.language.JeppValue;

public class BinaryFunc<A extends JeppValue, B extends JeppValue> implements BuiltinMethod {
    private final JeppType t, u, r;
    private final String name;
    private final apply<A, B> func;

    public interface apply<A extends JeppValue, B extends JeppValue> {
        JeppValue apply(A a, B b);
    }

    BinaryFunc(JeppType a, JeppType b, JeppType r, String name, apply<A, B> func) {
        this.t = a;
        this.u = b;
        this.r = r;
        this.name = name;
        this.func = func;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public final JeppMethodSignature signature() {
        return new JeppMethodSignature(t, u);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final JeppValue apply(JeppInterpreter interpreter, JeppValue... values) {
        return func.apply((A) values[0], (B) values[1]);
    }
}
