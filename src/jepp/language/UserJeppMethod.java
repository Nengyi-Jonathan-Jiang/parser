package jepp.language;

import compiler.parser.ParseTreeNode;
import jepp.interpreter.JeppInterpreter;

public class UserJeppMethod implements JeppMethod {
    private final String name;
    private final JeppMethodPrototype prototype;
    private final ParseTreeNode code;

    public UserJeppMethod(String name, JeppMethodPrototype prototype, ParseTreeNode code) {
        this.name = name;
        this.prototype = prototype;
        this.code = code;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public JeppMethodSignature signature() {
        return prototype.signature();
    }

    @Override
    public JeppType returnType() {
        return prototype.returnType();
    }

    @Override
    public JeppValue apply(JeppInterpreter interpreter, JeppValue... values) {
        JeppScope s = interpreter.pushNewScope();
        for(int i = 0; i < signature().types().length; i++) {

        }
        return null;
    }
}