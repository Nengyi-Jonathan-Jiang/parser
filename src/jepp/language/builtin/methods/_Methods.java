package jepp.language.builtin.methods;

import static jepp.language.builtin.types.PrimitiveJeppType.*;
import static jepp.language.builtin.types.PrimitiveJeppValue.*;

public class _Methods {
//    public static final BuiltinMethod[] casts = {
//            // Cast Int <-> Double
//            new UnaryOp<JInteger>(JIntegerT, JDoubleT, " as", a -> new JDouble(a.value)),
//            new UnaryOp<JDouble>(JDoubleT, JIntegerT, " as", a -> new JInteger((int)(Math.floor(a.value)))),
//            // Cast Boolean <-> Double
//            new UnaryOp<JDouble>(JDoubleT, JBooleanT, " as", a -> new JBoolean(a.value != 0)),
//            new UnaryOp<JBoolean>(JBooleanT, JDoubleT, " as", a -> new JDouble(a.value ? 1 : 0)),
//            // Cast Int <-> Boolean
//            new UnaryOp<JInteger>(JIntegerT, JBooleanT, " as", a -> new JBoolean(a.value != 0)),
//            new UnaryOp<JBoolean>(JBooleanT, JIntegerT, " as", a -> new JInteger(a.value ? 1 : 0)),
//            // Cast Compare --> Boolean
//            new UnaryOp<JCompare>(JCompareT, JBooleanT, " as", a -> new JBoolean(a.asNumber() == 0)),
//            // Cast Compare --> Int
//            new UnaryOp<JCompare>(JCompareT, JIntegerT, " as", a -> new JInteger(a.asNumber())),
//            // Cast Compare --> Double
//            new UnaryOp<JCompare>(JCompareT, JDoubleT, " as", a -> new JDouble(a.asNumber())),
//            // Cast Int <-> String
//            new UnaryOp<JInteger>(JIntegerT, JStringT, " as", a -> new JString(String.valueOf(a))),
//            new UnaryOp<JString>(JStringT, JIntegerT, " as", a -> new JInteger(Integer.parseInt(a.value))),
//            // Cast Double <-> String
//            new UnaryOp<JDouble>(JDoubleT, JStringT, " as", a -> new JString(String.valueOf(a))),
//            new UnaryOp<JString>(JStringT, JDoubleT, " as", a -> new JDouble(Double.parseDouble(a.value))),
//            // Cast Boolean <-> String
//            new UnaryOp<JBoolean>(JBooleanT, JStringT, " as", a -> new JString(String.valueOf(a))),
//            new UnaryOp<JString>(JStringT, JBooleanT, " as", a -> new JBoolean(Boolean.parseBoolean(a.value))),
//    };

    public static final BuiltinMethod[] arithmetic = {
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
    };

    public static final BuiltinMethod[] arithmetic_methods = {
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