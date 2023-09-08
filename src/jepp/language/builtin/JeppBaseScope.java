package jepp.language.builtin;

import jepp.language.JeppScope;
import jepp.language.builtin.methods._Methods;

import java.util.List;

public final class JeppBaseScope extends JeppScope {
    public JeppBaseScope() {
        super(null);
//        List.of(_Methods.casts).forEach(this::registerMethod);
        List.of(_Methods.concat).forEach(this::registerMethod);
        List.of(_Methods.arithmetic).forEach(this::registerMethod);
        List.of(_Methods.arithmetic_methods).forEach(this::registerMethod);
    }
}