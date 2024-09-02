package frontend.parser.lr_parser.parsing_table;

import frontend.Token;
import frontend.parser.ParseTreeNode;

import java.util.Deque;

public class AcceptEntry implements ActionTableEntry {

    public void writeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(" a");
    }

    public void applyAction(Deque<Integer> stateStack, Deque<ParseTreeNode> parseTreeNodeStack, ParsingTable table, Token token) {
    }

    @Override
    public boolean isAccepting() {
        return true;
    }

    @Override
    public boolean isDoneProcessingToken() {
        return true;
    }
}
