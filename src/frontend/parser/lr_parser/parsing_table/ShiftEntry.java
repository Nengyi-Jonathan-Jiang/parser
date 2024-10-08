package frontend.parser.lr_parser.parsing_table;

import frontend.Token;
import frontend.parser.ParseTreeNode;

import java.util.Stack;

public record ShiftEntry(int nextState) implements ActionTableEntry {

    public void serializeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(" s ");
        stringBuilder.append(nextState);
    }

    public void applyAction(Stack<Integer> stateStack, Stack<ParseTreeNode> parseTreeNodeStack, ParsingTable table, Token token) {
        stateStack.push(nextState);
        parseTreeNodeStack.push(new ParseTreeNode(token.type, token));
    }

    @Override
    public boolean isDoneProcessingToken() {
        return true;
    }

    @Override
    public boolean isAccepting() {
        return false;
    }
}
