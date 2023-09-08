package jepp.language.builtin.types;

import jepp.language.JeppType;

import java.util.Collections;
import java.util.Map;

public abstract class PrimitiveJeppType implements JeppType {
    @Override
    public String namespace() {
        return "jepp.lang.primitives";
    }

    @Override
    public Map<String, JeppType> memberVariableTypes() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return simpleName();
    }

    public static final PrimitiveJeppType JVoidT = new PrimitiveJeppType() {
        @Override
        public String simpleName() {
            return "void";
        }
    };
    public static final PrimitiveJeppType JIntegerT = new PrimitiveJeppType() {
        @Override
        public String simpleName() {
            return "int";
        }
    };
    public static final PrimitiveJeppType JDoubleT = new PrimitiveJeppType() {
        @Override
        public String simpleName() {
            return "double";
        }
    };
    public static final PrimitiveJeppType JStringT = new PrimitiveJeppType() {
        @Override
        public String simpleName() {
            return "string";
        }
    };
    public static final PrimitiveJeppType JBoolT = new PrimitiveJeppType() {
        @Override
        public String simpleName() {
            return "bool";
        }
    };
    public static final PrimitiveJeppType JCompareT = new PrimitiveJeppType() {
        @Override
        public String simpleName() {
            return "compare";
        }
    };
}