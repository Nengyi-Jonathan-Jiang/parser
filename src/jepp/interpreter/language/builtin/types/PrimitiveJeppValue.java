package jepp.interpreter.language.builtin.types;

import jepp.interpreter.JeppInterpreterException;
import jepp.interpreter.language.JeppValue;

public abstract sealed class PrimitiveJeppValue implements JeppValue permits PrimitiveJeppValue.JBoolean, PrimitiveJeppValue.JCompare, PrimitiveJeppValue.JFloat, PrimitiveJeppValue.JInteger, PrimitiveJeppValue.JVoid {
    private final PrimitiveJeppType type;

    protected PrimitiveJeppValue(PrimitiveJeppType type) {
        this.type = type;
    }

    @Override
    public PrimitiveJeppType getType() {
        return type;
    }

    @Override
    public JeppValue[] getData() {
        return new JeppValue[0];
    }

    public static final class JInteger extends PrimitiveJeppValue {
        public final int value;

        public JInteger(int value) {
            super(PrimitiveJeppType.JIntegerT);
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean isTruthy() {
            return value != 0;
        }
    }

    public static final class JFloat extends PrimitiveJeppValue {
        public final double value;

        public JFloat(double value) {
            super(PrimitiveJeppType.JFloatT);
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean isTruthy() {
            return value != 0;
        }
    }

    public static final class JBoolean extends PrimitiveJeppValue {
        public final boolean value;

        public JBoolean(boolean value) {
            super(PrimitiveJeppType.JBooleanT);
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean isTruthy() {
            return value;
        }
    }

    public static sealed class JVoid extends PrimitiveJeppValue permits SIG_break, SIG_continue {
        private JVoid() {
            super(PrimitiveJeppType.JVoidT);
        }

        @Override
        public String toString() {
            return "void";
        }

        @Override
        public boolean isTruthy() {
            return false;
        }
    }

    public static JVoid Void = new JVoid();
    public static final class SIG_break extends JVoid {
        public final int level;
        public SIG_break() { this(1); }
        public SIG_break(int level) {
            this.level = level;
        }
        public SIG_break decrease_level() {
            return new SIG_break(level - 1);
        }
    };
    public static final class SIG_continue extends JVoid {
        public final int level;
        public SIG_continue() { this(1); }
        public SIG_continue(int levels) {
            this.level = levels;
        }
        public SIG_continue decrease_level() {
            return new SIG_continue(level - 1);
        }
    };

    public static final class JCompare extends PrimitiveJeppValue {
        public enum CompareResult {
            LESS, EQUAL, GREATER;
            @Override
            public String toString() {
                return super.toString().toLowerCase();
            }
        }

        public enum CompareCondition {
            LESS, EQUAL, GREATER, LEQ, GEQ, NEQ;
            @Override
            public String toString() {
                return super.toString().toLowerCase();
            }
            public static CompareCondition fromString(String s) {
                return switch (s) {
                    case "<" -> LESS;
                    case "<=" -> LEQ;
                    case ">" -> GREATER;
                    case ">=" -> GEQ;
                    case "==" -> EQUAL;
                    case "!=" -> NEQ;
                    default -> throw new JeppInterpreterException("Unknown comparison condition " + s);
                };
            }
        }

        public final CompareResult value;

        public JCompare(CompareResult value) {
            super(PrimitiveJeppType.JCompareT);
            this.value = value;
        }

        public boolean matches(CompareCondition condition) {
            return switch (value) {
                case LESS -> switch (condition) { default -> false; case LESS, LEQ, NEQ -> true; };
                case EQUAL -> switch (condition) { default -> false; case EQUAL, LEQ, GEQ -> true; };
                case GREATER -> switch (condition) { default -> false; case GREATER, GEQ, NEQ -> true; };
            };
        }

        public int asNumber() {
            return switch (value) {
                case LESS -> -1;
                case EQUAL -> 0;
                case GREATER -> 1;
            };
        }

        @Override
        public String toString() {
            return "void";
        }

        @Override
        public boolean isTruthy() {
            return true;
        }
    }
}
