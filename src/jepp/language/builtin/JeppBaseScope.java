package jepp.language.builtin;

import jepp.language.JeppScope;
import jepp.language.builtin.methods._Methods;

import java.util.List;

public final class JeppBaseScope extends JeppScope {
    public JeppBaseScope() {
        super(null);
        List.of(_Methods.methods).forEach(this::registerMethod);
    }
}