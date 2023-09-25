package jepp.interpreter.language;

import jepp.interpreter.JeppInterpreter;
import org.jetbrains.annotations.NotNull;

public interface JeppMethod extends Comparable<JeppMethod>{
    @NotNull String name();
    @NotNull JeppMethodSignature signature();

    @NotNull JeppValue apply(@NotNull JeppInterpreter interpreter, JeppValue... values);

    @Override
    default int compareTo(JeppMethod o) {
        return signature().compareTo(o.signature());
    }
}