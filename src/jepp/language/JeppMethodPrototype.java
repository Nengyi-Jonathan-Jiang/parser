package jepp.language;

import java.util.Arrays;

public record JeppMethodPrototype(JeppMethodSignature signature, String[] argNames) {
    @Override
    public String toString() {
        return "JeppMethodPrototype[" +
                "signature=" + signature + ", " +
                "argNames=" + Arrays.toString(argNames) + ']';
    }
}