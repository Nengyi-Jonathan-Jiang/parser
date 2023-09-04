package jepp.language;

import java.util.Arrays;

public record JeppMethodSignature(JeppType... types) implements Comparable<JeppMethodSignature> {
    @Override
    public int compareTo(JeppMethodSignature o) {
        return Arrays.compare(types, o.types);
    }

    @Override
    public String toString() {
        return Arrays.toString(types);
    }
}