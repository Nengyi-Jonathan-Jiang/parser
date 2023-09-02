package jepp.language;

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

    public static final class JString extends PrimitiveJeppValue {
        public final String value;
        public JString(String value) {
            super(PrimitiveJeppType.JStringT);
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    public static final class JBoolean extends PrimitiveJeppValue {
        public final boolean value;
        public JBoolean(boolean value) {
            super(PrimitiveJeppType.JBoolT);
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
}
