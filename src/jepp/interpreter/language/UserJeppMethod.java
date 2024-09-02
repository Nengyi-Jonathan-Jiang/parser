package jepp.interpreter.language;

import frontend.parser.ParseTreeNode;
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
    public JeppValue apply(JeppInterpreter interpreter, JeppValue... values) {
        JeppScope s = interpreter.pushNewScope();
        for(int i = 0; i < signature().types().length; i++) {
            s.declareVariable(prototype.argNames()[i]);
            s.setVariable(prototype.argNames()[i], values[i]);
        }
        JeppValue res = interpreter.evaluate(code);
        interpreter.popScope(s);
        return res;
    }
}