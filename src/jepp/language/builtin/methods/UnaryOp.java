package jepp.language.builtin.methods;

import jepp.language.JeppType;
import jepp.language.JeppValue;

public class UnaryOp<X extends JeppValue> extends UnaryFunc<X> {
    public UnaryOp(JeppType x, JeppType r, String op, unaryFunc<X> func) {
        super(x, r, "operator" + op, func);
    }
}
