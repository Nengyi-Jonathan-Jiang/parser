package util;

import java.util.function.Supplier;

public class Helpers {
    private Helpers() {}

    /**
     * Get the result of a lambda function with no parameters.
     * Used to allow the IIFE (Immediately Invoked Function Expression) idiom
     */
    public static <T> T evaluate(Supplier<T> f) {
        return f.get();
    }
}
