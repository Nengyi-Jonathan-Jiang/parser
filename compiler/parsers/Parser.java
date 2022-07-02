package compiler.parsers;

import compiler.Token;

/**
 * Parses an array of tokens into a parse tree
 */
public interface Parser {
    ParseTree parse(Token[] tokens);
}
