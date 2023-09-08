package jepp.interpreter;

import jepp.language.JeppValue;

public class JeppUnknownOperatorException extends JeppInterpreterException {
    public JeppUnknownOperatorException(String s, JeppValue val1, JeppValue val2) {
        super("Unknown operator " + s + " on types " + val1.getType().fullName() +  " and " + val2.getType().fullName());
    }
}
