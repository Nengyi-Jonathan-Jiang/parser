package jepp.language;

public interface JeppMethod {
    String name();
    JeppMethodSignature signature();
    JeppType returnType();
    JeppValue apply(JeppScope scope, JeppValue... values);
}