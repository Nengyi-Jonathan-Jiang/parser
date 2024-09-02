package frontend.parser.lr_parser.parsing_table;

import frontend.Token;
import frontend.parser.ParseTreeNode;

import java.util.Deque;

public record ShiftEntry(int nextState) implements ActionTableEntry {

    public void writeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(" s ");
        stringBuilder.append(nextState);
    }

    public void applyAction(Deque<Integer> stateStack, Deque<ParseTreeNode> parseTreeNodeStack, ParsingTable table, Token token) {
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
