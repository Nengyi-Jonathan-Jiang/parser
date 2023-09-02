package jepp.language;

import java.util.Arrays;

public class JeppMethodSignature implements Comparable<JeppMethodSignature>{
    PrimitiveJeppType[] types;

    @Override
    public int compareTo(JeppMethodSignature o) {
        return Arrays.compare(types, o.types);
    }
}