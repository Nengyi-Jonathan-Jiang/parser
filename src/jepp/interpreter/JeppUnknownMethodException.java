package jepp.interpreter;

import jepp.language.JeppType;

import java.util.List;

public class JeppUnknownMethodException extends JeppInterpreterException {
    public JeppUnknownMethodException(String methodName, List<JeppType> argTypes) {
        super("Unknown method " + methodName + " for types " + argTypes.stream().map(JeppType::fullName).toList());
    }
}
