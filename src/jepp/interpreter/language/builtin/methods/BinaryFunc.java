package jepp.interpreter.language.builtin.methods;

import jepp.interpreter.JeppInterpreter;
import jepp.interpreter.language.JeppMethodSignature;
import jepp.interpreter.language.JeppType;
import jepp.interpreter.language.JeppValue;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull String name() {
        return name;
    }

    @Override
    public final @NotNull JeppMethodSignature signature() {
        return new JeppMethodSignature(t, u);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull JeppValue apply(@NotNull JeppInterpreter interpreter, JeppValue... values) {
        return func.apply((A) values[0], (B) values[1]);
    }
}
