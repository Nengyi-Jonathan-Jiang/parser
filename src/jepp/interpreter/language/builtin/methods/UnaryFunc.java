package jepp.interpreter.language.builtin.methods;

import jepp.interpreter.JeppInterpreter;
import jepp.interpreter.language.JeppMethodSignature;
import jepp.interpreter.language.JeppType;
import jepp.interpreter.language.JeppValue;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull String name() {
        return name;
    }

    @Override
    public final @NotNull JeppMethodSignature signature() {
        return new JeppMethodSignature(x);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull JeppValue apply(@NotNull JeppInterpreter interpreter, JeppValue... values) {
        return func.apply((X) values[0]);
    }
}
