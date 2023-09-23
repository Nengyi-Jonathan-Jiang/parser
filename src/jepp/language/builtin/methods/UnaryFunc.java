package jepp.language.builtin.methods;

import jepp.interpreter.JeppInterpreter;
import jepp.language.JeppMethodSignature;
import jepp.language.JeppType;
import jepp.language.JeppValue;

public class UnaryFunc<X extends JeppValue> implements BuiltinMethod {
    private final JeppType x, r;
    private final String name;
    private final unaryFunc<X> func;

    public interface unaryFunc<X extends JeppValue> {
        JeppValue apply(X x);
    }

    public UnaryFunc(JeppType x, JeppType r, String name, unaryFunc<X> func) {
        this.x = x;
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
        return new JeppMethodSignature(x);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final JeppValue apply(JeppInterpreter interpreter, JeppValue... values) {
        return func.apply((X) values[0]);
    }
}
