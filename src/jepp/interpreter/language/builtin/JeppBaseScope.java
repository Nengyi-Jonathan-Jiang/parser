package jepp.interpreter.language.builtin;

import jepp.interpreter.language.JeppScope;
import jepp.interpreter.language.builtin.methods._Methods;

import java.util.List;

public final class JeppBaseScope extends JeppScope {
    public JeppBaseScope() {
        super(null);
//        List.of(_Methods.casts).forEach(this::registerMethod);
        List.of(_Methods.arithmetic).forEach(this::registerMethod);
        List.of(_Methods.arithmetic_methods).forEach(this::registerMethod);
        List.of(_Methods.compare_methods).forEach(this::registerMethod);
    }
}