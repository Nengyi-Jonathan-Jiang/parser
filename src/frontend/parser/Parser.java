package frontend.parser;

import frontend.Token;

/**
 * Parses an array of tokens into a parse tree
 */
public interface Parser {
    ParseTreeNode parse(Token[] tokens);

    Parse start();

    interface Parse{
        void process(Token tok);
        boolean didAccept();
        ParseTreeNode getResult();
    }
}