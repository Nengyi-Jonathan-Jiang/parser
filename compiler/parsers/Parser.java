package compiler.parsers;

public interface Parser {
    public ParseTree parse(String[] tokens);
}
