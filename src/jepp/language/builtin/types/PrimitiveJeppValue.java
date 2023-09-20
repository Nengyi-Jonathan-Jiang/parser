package jepp.language.builtin.types;

import jepp.language.JeppValue;

public abstract class PrimitiveJeppValue implements JeppValue {
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
    }

    public static final class JDouble extends PrimitiveJeppValue {
        public final double value;

        public JDouble(double value) {
            super(PrimitiveJeppType.JDoubleT);
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
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
    }

    public static final class JVoid extends PrimitiveJeppValue {
        private JVoid() {
            super(PrimitiveJeppType.JVoidT);
        }

        @Override
        public String toString() {
            return "void";
        }
    }

    public static JVoid Void = new JVoid();

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
        }

        public final CompareResult value;

        private JCompare(CompareResult value) {
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
    }
}
