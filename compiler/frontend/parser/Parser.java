package compiler.frontend.parser;

import compiler.frontend.Token;

/**
 * Parses an array of tokens into a parse tree
 */
public interface Parser {
    ParseTree parse(Token[] tokens);

    Parse start();

    interface Parse{
        void process(Token tok);
        boolean isFinished();
        ParseTree getParseTree();
    }
}