package jepp.interpreter.language.builtin.methods;

import jepp.interpreter.language.JeppType;
import jepp.interpreter.language.JeppValue;

public class BinaryOp<A extends JeppValue, B extends JeppValue> extends BinaryFunc<A, B> {
    public BinaryOp(JeppType a, JeppType b, JeppType r, String op, apply<A, B> func) {
        super(a, b, r, "operator" + op, func);
    }
}
