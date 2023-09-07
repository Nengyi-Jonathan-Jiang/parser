package jepp.language.builtin.methods;

import jepp.language.*;

import java.util.function.Function;

public class BinaryFunc<A extends JeppValue, B extends JeppValue> extends BuiltinJeppMethod {
    private final binaryFuncLambda<A, B> func;

    public interface binaryFuncLambda <A extends JeppValue, B extends JeppValue> {
        JeppValue apply(A a, B b);
    }

    public BinaryFunc(String name, JeppType a, JeppType b, JeppType r, binaryFuncLambda<A, B> func) {
        super(name, new JeppMethodSignature(a, b), r);
        this.func = func;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JeppValue apply(JeppScope scope, JeppValue... values) {
        return func.apply((A) values[0], (B) values[1]);
    }
}
