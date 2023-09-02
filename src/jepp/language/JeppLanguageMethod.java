package jepp.language;

public abstract class JeppLanguageMethod implements JeppMethod {
    public static JeppLanguageMethod add_int_int = new JeppLanguageMethod() {
        @Override
        public String name() {
            return "operator+";
        }

        @Override
        public JeppMethodSignature signature() {
            return new JeppMethodSignature(PrimitiveJeppType.JIntegerT, PrimitiveJeppType.JIntegerT);
        }

        @Override
        public JeppType returnType() {
            return PrimitiveJeppType.JIntegerT;
        }

        @Override
        public JeppValue apply(JeppScope scope, JeppValue... values) {
            return new PrimitiveJeppValue.JInteger(
                ((PrimitiveJeppValue.JInteger)values[0]).value + ((PrimitiveJeppValue.JInteger)values[0]).value
            );
        }
    };
    public static JeppLanguageMethod add_double_double = new JeppLanguageMethod() {
        @Override
        public String name() {
            return "operator+";
        }

        @Override
        public JeppMethodSignature signature() {
            return new JeppMethodSignature(PrimitiveJeppType.JDoubleT, PrimitiveJeppType.JDoubleT);
        }

        @Override
        public JeppType returnType() {
            return PrimitiveJeppType.JDoubleT;
        }

        @Override
        public JeppValue apply(JeppScope scope, JeppValue... values) {
            return new PrimitiveJeppValue.JDouble(
                ((PrimitiveJeppValue.JDouble)values[0]).value + ((PrimitiveJeppValue.JDouble)values[0]).value
            );
        }
    };
    public static JeppLanguageMethod cast_int_double = new JeppLanguageMethod() {
        @Override
        public String name() {
            return "cast";
        }

        @Override
        public JeppMethodSignature signature() {
            return new JeppMethodSignature(PrimitiveJeppType.JIntegerT);
        }

        @Override
        public JeppType returnType() {
            return PrimitiveJeppType.JDoubleT;
        }

        @Override
        public JeppValue apply(JeppScope scope, JeppValue... values) {
            return new PrimitiveJeppValue.JDouble(((PrimitiveJeppValue.JInteger)values[0]).value);
        }
    };
}