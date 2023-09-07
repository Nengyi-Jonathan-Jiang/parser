package jepp.language.builtin.methods;

import jepp.language.*;
import jepp.language.builtin.types.PrimitiveJeppType;
import jepp.language.builtin.types.PrimitiveJeppValue;
import jepp.language.builtin.types.PrimitiveJeppValue.*;
import static jepp.language.builtin.types.PrimitiveJeppType.*;

public abstract class BuiltinMethods implements JeppMethod {
    private static double mod(double a, double b) {
        return (a % b + b) % b;
    }
    private static int mod(int a, int b) {
        return (a % b + b) % b;
    }

    public static BuiltinJeppMethod[] methods = {
        // String concatenation
        new BinaryFunc<JString , JInteger>("operator+", JStringT , JIntegerT, JStringT, (a, b) -> new JString(a.value + b.value)),
        new BinaryFunc<JString , JDouble >("operator+", JStringT , JDoubleT , JStringT, (a, b) -> new JString(a.value + b.value)),
        new BinaryFunc<JString , JBoolean>("operator+", JStringT , JBooleanT, JStringT, (a, b) -> new JString(a.value + b.value)),
        new BinaryFunc<JInteger, JString >("operator+", JIntegerT, JStringT , JStringT, (a, b) -> new JString(a.value + b.value)),
        new BinaryFunc<JDouble , JString >("operator+", JDoubleT , JStringT , JStringT, (a, b) -> new JString(a.value + b.value)),
        new BinaryFunc<JBoolean , JString>("operator+", JBooleanT, JStringT , JStringT, (a, b) -> new JString(a.value + b.value)),
        new BinaryFunc<JCompare , JString>("operator+", JBooleanT, JStringT , JStringT, (a, b) -> new JString(a.value + b.value)),
        new BinaryFunc<JString , JString >("operator+", JStringT , JStringT , JStringT, (a, b) -> new JString(a.value + b.value)),

        // Addition
        new BinaryFunc<JInteger, JInteger>("operator+", JIntegerT, JIntegerT, JIntegerT, (a, b) -> new JInteger(a.value + b.value)),
        new BinaryFunc<JDouble, JInteger> ("operator+", JDoubleT , JIntegerT, JIntegerT, (a, b) -> new JDouble (a.value + b.value)),
        new BinaryFunc<JInteger, JDouble> ("operator+", JIntegerT, JDoubleT , JDoubleT , (a, b) -> new JDouble (a.value + b.value)),
        new BinaryFunc<JDouble, JDouble>  ("operator+", JDoubleT , JDoubleT , JDoubleT , (a, b) -> new JDouble (a.value + b.value)),

        // Subtraction
        new BinaryFunc<JInteger, JInteger>("operator-", JIntegerT, JIntegerT, JIntegerT, (a, b) -> new JInteger(a.value - b.value)),
        new BinaryFunc<JDouble, JInteger> ("operator-", JDoubleT , JIntegerT, JIntegerT, (a, b) -> new JDouble (a.value - b.value)),
        new BinaryFunc<JInteger, JDouble> ("operator-", JIntegerT, JDoubleT , JDoubleT , (a, b) -> new JDouble (a.value - b.value)),
        new BinaryFunc<JDouble, JDouble>  ("operator-", JDoubleT , JDoubleT , JDoubleT , (a, b) -> new JDouble (a.value - b.value)),

        // Multiplication
        new BinaryFunc<JInteger, JInteger>("operator*", JIntegerT, JIntegerT, JIntegerT, (a, b) -> new JInteger(a.value * b.value)),
        new BinaryFunc<JDouble, JInteger> ("operator*", JDoubleT , JIntegerT, JIntegerT, (a, b) -> new JDouble (a.value * b.value)),
        new BinaryFunc<JInteger, JDouble> ("operator*", JIntegerT, JDoubleT , JDoubleT , (a, b) -> new JDouble (a.value * b.value)),
        new BinaryFunc<JDouble, JDouble>  ("operator*", JDoubleT , JDoubleT , JDoubleT , (a, b) -> new JDouble (a.value * b.value)),

        // Division
        new BinaryFunc<JInteger, JInteger>("operator/", JIntegerT, JIntegerT, JIntegerT, (a, b) -> new JInteger(a.value / b.value)),
        new BinaryFunc<JDouble, JInteger> ("operator/", JDoubleT , JIntegerT, JIntegerT, (a, b) -> new JDouble (a.value / b.value)),
        new BinaryFunc<JInteger, JDouble> ("operator/", JIntegerT, JDoubleT , JDoubleT , (a, b) -> new JDouble (a.value / b.value)),
        new BinaryFunc<JDouble, JDouble>  ("operator/", JDoubleT , JDoubleT , JDoubleT , (a, b) -> new JDouble (a.value / b.value)),

        // Modulo
        new BinaryFunc<JInteger, JInteger>("operator/", JIntegerT, JIntegerT, JIntegerT, (a, b) -> new JInteger(mod(a.value, b.value))),
        new BinaryFunc<JDouble, JInteger> ("operator/", JDoubleT , JIntegerT, JIntegerT, (a, b) -> new JDouble (mod(a.value, b.value))),
        new BinaryFunc<JInteger, JDouble> ("operator/", JIntegerT, JDoubleT , JDoubleT , (a, b) -> new JDouble (mod(a.value, b.value))),
        new BinaryFunc<JDouble, JDouble>  ("operator/", JDoubleT , JDoubleT , JDoubleT , (a, b) -> new JDouble (mod(a.value, b.value))),

        // Equality
        new BinaryFunc<JInteger, JInteger>("operator<=>", JIntegerT, JIntegerT, JIntegerT, (a, b) -> new JBoolean(a.value == b.value)),
        new BinaryFunc<JDouble, JInteger> ("operator<=>", JDoubleT , JIntegerT, JIntegerT, (a, b) -> new JDouble (mod(a.value, b.value))),
        new BinaryFunc<JInteger, JDouble> ("operator<=>", JIntegerT, JDoubleT , JDoubleT , (a, b) -> new JDouble (mod(a.value, b.value))),
        new BinaryFunc<JDouble, JDouble>  ("operator<=>", JDoubleT , JDoubleT , JDoubleT , (a, b) -> new JDouble (mod(a.value, b.value))),

        // Comparison
        new BinaryFunc<JInteger, JInteger>("operator<=>", JIntegerT, JIntegerT, JIntegerT, (a, b) -> new JInteger(mod(a.value, b.value))),
        new BinaryFunc<JDouble, JInteger> ("operator<=>", JDoubleT , JIntegerT, JIntegerT, (a, b) -> new JDouble (mod(a.value, b.value))),
        new BinaryFunc<JInteger, JDouble> ("operator<=>", JIntegerT, JDoubleT , JDoubleT , (a, b) -> new JDouble (mod(a.value, b.value))),
        new BinaryFunc<JDouble, JDouble>  ("operator<=>", JDoubleT , JDoubleT , JDoubleT , (a, b) -> new JDouble (mod(a.value, b.value))),
    };
}