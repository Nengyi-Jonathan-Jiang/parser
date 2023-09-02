package jepp.language;

import java.util.Arrays;

public record JeppMethodSignature(PrimitiveJeppType... types) implements Comparable<JeppMethodSignature> {
    @Override
    public int compareTo(JeppMethodSignature o) {
        return Arrays.compare(types, o.types);
    }
}