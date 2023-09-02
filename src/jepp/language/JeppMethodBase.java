package jepp.language;

public interface JeppMethodBase {
    String getName();
    JeppMethodSignature getSignature();
    JeppValue apply(JeppScope scope, JeppValue... values);
}