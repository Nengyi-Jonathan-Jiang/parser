package jepp.language;

import java.util.Collections;
import java.util.Map;

public interface PrimitiveJeppType extends JeppType {
    @Override
    default String namespace() {
        return "jepp.lang.primitives";
    }
    @Override
    default Map<String, JeppType> memberVariableTypes() {
        return Collections.emptyMap();
    }

    PrimitiveJeppType JVoidT = () -> "void";
    PrimitiveJeppType JIntegerT = () -> "int";
    PrimitiveJeppType JDoubleT = () -> "double";
    PrimitiveJeppType JStringT = () -> "string";
    PrimitiveJeppType JBoolT = () -> "bool";
}