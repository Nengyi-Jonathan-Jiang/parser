package jepp.language;

import jepp.language.builtin.methods.BuiltinJeppMethod;
import jepp.language.builtin.methods.BuiltinMethods;

public final class JeppBaseScope extends jepp.language.JeppScope {
    private JeppBaseScope() {
        super(null);
        for (BuiltinJeppMethod method : BuiltinMethods.methods) {
            registerMethod(method);
        }
    }

    private static JeppBaseScope instance;

    public static JeppBaseScope getInstance() {
        if(instance == null) instance = new JeppBaseScope();
        return instance;
    }
}