package jepp.language;

public interface JeppMethod extends Comparable<JeppMethod>{
    String name();
    JeppMethodSignature signature();
    JeppType returnType();
    JeppValue apply(JeppScope scope, JeppValue... values);

    @Override
    default int compareTo(JeppMethod o) {
        return signature().compareTo(o.signature());
    }
}