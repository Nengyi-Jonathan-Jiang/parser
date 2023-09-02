package jepp.language;

import java.util.Map;

public interface JeppType extends Comparable<JeppType> {
    String namespace();
    String simpleName();
    default String fullName() {
        return namespace() + "." + simpleName();
    }
    Map<String, JeppType> memberVariableTypes();

    @Override
    default int compareTo(JeppType o) {
        return fullName().compareTo(o.fullName());
    }
}