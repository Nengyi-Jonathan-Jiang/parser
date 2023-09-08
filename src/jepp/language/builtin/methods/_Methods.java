package jepp.language.builtin.methods;

import static jepp.language.builtin.types.PrimitiveJeppType.*;
import static jepp.language.builtin.types.PrimitiveJeppValue.*;

public class _Methods {
    public static final BuiltinMethod[] methods = {
            // String concatenation
            new BinaryOp<JInteger, JString>(JIntegerT, JStringT, JStringT, "+", (a, b) -> new JString(a.value + b.value)),
            new BinaryOp<JDouble, JString>(JDoubleT, JStringT, JStringT, "+", (a, b) -> new JString(a.value + b.value)),
            new BinaryOp<JString, JInteger>(JStringT, JIntegerT, JStringT, "+", (a, b) -> new JString(a.value + b.value)),
            new BinaryOp<JString, JDouble>(JStringT, JDoubleT, JStringT, "+", (a, b) -> new JString(a.value + b.value)),
            new BinaryOp<JString, JString>(JStringT, JStringT, JStringT, "+", (a, b) -> new JString(a.value + b.value)),
            // Addition
            new BinaryOp<JInteger, JInteger>(JIntegerT, JIntegerT, JIntegerT, "+", (a, b) -> new JInteger(a.value + b.value)),
            new BinaryOp<JInteger, JDouble>(JIntegerT, JDoubleT, JDoubleT, "+", (a, b) -> new JDouble(a.value + b.value)),
            new BinaryOp<JDouble, JInteger>(JDoubleT, JIntegerT, JDoubleT, "+", (a, b) -> new JDouble(a.value + b.value)),
            new BinaryOp<JDouble, JDouble>(JDoubleT, JDoubleT, JDoubleT, "+", (a, b) -> new JDouble(a.value + b.value)),
            // Subtraction
            new BinaryOp<JInteger, JInteger>(JIntegerT, JIntegerT, JIntegerT, "-", (a, b) -> new JInteger(a.value - b.value)),
            new BinaryOp<JInteger, JDouble>(JIntegerT, JDoubleT, JDoubleT, "-", (a, b) -> new JDouble(a.value - b.value)),
            new BinaryOp<JDouble, JInteger>(JDoubleT, JIntegerT, JDoubleT, "-", (a, b) -> new JDouble(a.value - b.value)),
            new BinaryOp<JDouble, JDouble>(JDoubleT, JDoubleT, JDoubleT, "-", (a, b) -> new JDouble(a.value - b.value)),
            // Multiplication
            new BinaryOp<JInteger, JInteger>(JIntegerT, JIntegerT, JIntegerT, "*", (a, b) -> new JInteger(a.value * b.value)),
            new BinaryOp<JInteger, JDouble>(JIntegerT, JDoubleT, JDoubleT, "*", (a, b) -> new JDouble(a.value * b.value)),
            new BinaryOp<JDouble, JInteger>(JDoubleT, JIntegerT, JDoubleT, "*", (a, b) -> new JDouble(a.value * b.value)),
            new BinaryOp<JDouble, JDouble>(JDoubleT, JDoubleT, JDoubleT, "*", (a, b) -> new JDouble(a.value * b.value)),
            // Division
            new BinaryOp<JInteger, JInteger>(JIntegerT, JIntegerT, JIntegerT, "/", (a, b) -> new JInteger(a.value / b.value)),
            new BinaryOp<JInteger, JDouble>(JIntegerT, JDoubleT, JDoubleT, "/", (a, b) -> new JDouble(a.value / b.value)),
            new BinaryOp<JDouble, JInteger>(JDoubleT, JIntegerT, JDoubleT, "/", (a, b) -> new JDouble(a.value / b.value)),
            new BinaryOp<JDouble, JDouble>(JDoubleT, JDoubleT, JDoubleT, "/", (a, b) -> new JDouble(a.value / b.value)),
            // Modulo
            new BinaryOp<JInteger, JInteger>(JIntegerT, JIntegerT, JIntegerT, "%", (a, b) -> new JInteger((a.value % b.value + b.value) % b.value)),
            new BinaryOp<JInteger, JDouble>(JIntegerT, JDoubleT, JDoubleT, "%", (a, b) -> new JDouble((a.value % b.value + b.value) % b.value)),
            new BinaryOp<JDouble, JInteger>(JDoubleT, JIntegerT, JDoubleT, "%", (a, b) -> new JDouble((a.value % b.value + b.value) % b.value)),
            new BinaryOp<JDouble, JDouble>(JDoubleT, JDoubleT, JDoubleT, "%", (a, b) -> new JDouble((a.value % b.value + b.value) % b.value)),

            // Absolute Value
            new UnaryFunc<JInteger>(JIntegerT, JIntegerT, "abs", a -> new JInteger(Math.abs(a.value))),
            new UnaryFunc<JDouble>(JDoubleT, JDoubleT, "abs", a -> new JDouble(Math.abs(a.value))),
            // Min
            new BinaryFunc<JInteger, JInteger>(JIntegerT, JIntegerT, JIntegerT, "min", (a, b) -> new JInteger(Math.min(a.value, b.value))),
            new BinaryFunc<JInteger, JDouble>(JIntegerT, JDoubleT, JDoubleT, "min", (a, b) -> new JDouble(Math.min(a.value, b.value))),
            new BinaryFunc<JDouble, JInteger>(JDoubleT, JIntegerT, JDoubleT, "min", (a, b) -> new JDouble(Math.min(a.value, b.value))),
            new BinaryFunc<JDouble, JDouble>(JDoubleT, JDoubleT, JDoubleT, "min", (a, b) -> new JDouble(Math.min(a.value, b.value))),
            // Max
            new BinaryFunc<JInteger, JInteger>(JIntegerT, JIntegerT, JIntegerT, "max", (a, b) -> new JInteger(Math.max(a.value, b.value))),
            new BinaryFunc<JInteger, JDouble>(JIntegerT, JDoubleT, JDoubleT, "max", (a, b) -> new JDouble(Math.max(a.value, b.value))),
            new BinaryFunc<JDouble, JInteger>(JDoubleT, JIntegerT, JDoubleT, "max", (a, b) -> new JDouble(Math.max(a.value, b.value))),
            new BinaryFunc<JDouble, JDouble>(JDoubleT, JDoubleT, JDoubleT, "max", (a, b) -> new JDouble(Math.max(a.value, b.value))),
    };
}