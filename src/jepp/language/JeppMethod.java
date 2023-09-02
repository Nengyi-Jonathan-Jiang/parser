package jepp.language;

import compiler.parser.ParseTreeNode;

public class JeppMethod implements JeppMethodBase {
    private final String name;
    private final JeppMethodPrototype prototype;
    private final ParseTreeNode code;

    public JeppMethod(String name, JeppMethodPrototype prototype, ParseTreeNode code) {
        this.name = name;
        this.prototype = prototype;
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JeppMethodSignature getSignature() {
        return prototype.signature();
    }

    @Override
    public JeppValue apply(JeppScope scope, JeppValue... values) {
        return null;
    }
}