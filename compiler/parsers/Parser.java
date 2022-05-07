package compiler.parsers;

/**
 * Parses an array of tokens into a parse tree
 */
public interface Parser {
    ParseTree parse(String[] tokens);
}
