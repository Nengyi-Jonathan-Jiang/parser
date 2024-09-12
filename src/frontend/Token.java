package frontend;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Token {
    public final Symbol type;
    public final String value;
    public final Location startLocation;
    public final Location endLocation;

    public Token(Symbol type, String value, Location startLocation, Location endLocation) {
        this.type = type;
        this.value = value;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public String toString() {
        return type.name.equals(value) ? value : value.isEmpty() ? type.toString() : type + "<" + value + ">";
    }

    public boolean isOfType(Symbol type) {
        return this.type == type;
    }

    public String typeName() {
        return this.type.name;
    }

    public boolean typeNameEquals(String name) {
        return typeName().equals(name);
    }

    public boolean isEOF() {
        return type == type.getTable().__END__;
    }

    public Token map(UnaryOperator<String> mappingFunction) {
        return map(mappingFunction, type);
    }

    public Token mapIf(Predicate<Token> condition, UnaryOperator<String> mappingFunction) {
        return condition.test(this) ? map(mappingFunction, type) : this;
    }

    public Token map(UnaryOperator<String> mappingFunction, Symbol newTokenType) {
        return new Token(newTokenType, mappingFunction.apply(value), startLocation, endLocation);
    }
}