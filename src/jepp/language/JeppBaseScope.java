package jepp.language;

public final class JeppBaseScope extends jepp.language.JeppScope {
    public JeppBaseScope() {
        super(null);
        registerMethod(JeppLanguageMethod.add_int_int);
        registerMethod(JeppLanguageMethod.add_double_double);
        registerMethod(JeppLanguageMethod.cast_int_double);
    }
}
