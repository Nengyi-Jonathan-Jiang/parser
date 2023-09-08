package jepp.language;

import jepp.interpreter.JeppInterpreter;

public interface JeppMethod extends Comparable<JeppMethod>{
    String name();
    JeppMethodSignature signature();
    JeppType returnType();
    JeppValue apply(JeppInterpreter interpreter, JeppValue... values);

    @Override
    default int compareTo(JeppMethod o) {
        return signature().compareTo(o.signature());
    }
}