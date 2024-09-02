package jepp.interpreter.language.builtin.methods;

import jepp.interpreter.JeppInterpreter;
import jepp.interpreter.language.JeppMethodSignature;
import jepp.interpreter.language.JeppType;
import jepp.interpreter.language.JeppValue;
import jepp.interpreter.language.builtin.types.PrimitiveJeppValue.JCompare.CompareResult;

import static jepp.interpreter.language.builtin.types.PrimitiveJeppValue.JCompare;

public class JComparator<X extends JeppValue> implements BuiltinMethod {
    private final JeppType x;
    private final compare<X> func;

    public interface compare<X extends JeppValue> {
        CompareResult apply(X a, X b);
    }

    JComparator(JeppType x, compare<X> func) {
        this.x = x;
        this.func = func;
    }

    @Override
    public String name() {
        return "operator<=>";
    }

    @Override
    public final JeppMethodSignature signature() {
        return new JeppMethodSignature(x, x);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final JeppValue apply(JeppInterpreter interpreter, JeppValue... values) {
        return new JCompare(func.apply((X) values[0], (X) values[1]));
    }
}
