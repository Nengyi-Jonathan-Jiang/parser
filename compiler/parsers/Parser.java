package compiler.parsers;

import compiler.Token;

/**
 * Parses an array of tokens into a parse tree
 */
public interface Parser {
    ParseTree parse(Token[] tokens);

    Parse getParse();

    static interface Parse{
        void process(Token tok);
        boolean isFinished();
        ParseTree getParseTree();
    }
}