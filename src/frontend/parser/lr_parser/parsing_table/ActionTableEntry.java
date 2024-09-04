package frontend.parser.lr_parser.parsing_table;

import frontend.Token;
import frontend.parser.ParseTreeNode;
import frontend.util.SerializableToString;

import java.util.Stack;

public interface ActionTableEntry extends SerializableToString {
    void applyAction(Stack<Integer> stateStack, Stack<ParseTreeNode> parseTreeNodeStack, ParsingTable table, Token token);

    boolean isDoneProcessingToken();

    boolean isAccepting();
}
