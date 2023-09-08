package jepp.language.builtin.methods;

import jepp.language.JeppType;
import jepp.language.JeppValue;

public class BinaryOp<A extends JeppValue, B extends JeppValue> extends BinaryFunc<A, B> {
    public BinaryOp(JeppType a, JeppType b, JeppType r, String op, apply<A, B> func) {
        super(a, b, r, "operator" + op, func);
    }
}
