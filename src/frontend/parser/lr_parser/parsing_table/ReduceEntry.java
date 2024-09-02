package frontend.parser.lr_parser.parsing_table;

import frontend.Symbol;
import frontend.Token;
import frontend.parser.ParseTreeNode;
import frontend.parser.Rule;

import java.util.Arrays;
import java.util.Deque;
import java.util.stream.Stream;

public record ReduceEntry(Rule rule) implements ActionTableEntry {

    public void writeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(" r ");

        stringBuilder.append(rule.getRhsSize());
        stringBuilder.append(" ");

        if (rule.chained) stringBuilder.append("__c ");
        if (!rule.unwrap) stringBuilder.append("__w ");

        stringBuilder.append(rule.getLhs());
        stringBuilder.append(" ");
        stringBuilder.append(rule.getRhs().toString());
    }

    public void applyAction(Deque<Integer> stateStack, Deque<ParseTreeNode> parseTreeNodeStack, ParsingTable table, Token token) {
        doApply(stateStack, parseTreeNodeStack, table);
    }

    @Override
    public boolean isDoneProcessingToken() {
        return false;
    }

    @Override
    public boolean isAccepting() {
        return false;
    }

    private void doApply(Deque<Integer> stateStack, Deque<ParseTreeNode> parseTreeNodeStack, ParsingTable table) {
        Symbol lhs = rule.getLhs();

        // Update state stack
        for (int j = 0; j < rule.getRhsSize(); j++) stateStack.pop();
        GotoEntry gotoEntry = table.getGoto(stateStack.peek(), lhs);
        stateStack.push(gotoEntry.nextState());

        // Update parse tree - merge nodes into parent node

        // Unwrap node if allowed to simplify the parse tree
        if (rule.getRhs().size() == 1 && rule.unwrap) return;

        // Handle chained nodes
        ParseTreeNode[] children = new ParseTreeNode[rule.getRhsSize()];
        for (int j = rule.getRhsSize() - 1; j >= 0; j--) children[j] = parseTreeNodeStack.pop();
        if (rule.chained) {
            ParseTreeNode reducer = children[0];
            if (reducer.matches(lhs)) {
                var joinedChildren = Stream.concat(
                    reducer.children(),
                    Arrays.stream(children).skip(1)
                ).toArray(ParseTreeNode[]::new);
                parseTreeNodeStack.push(new ParseTreeNode(lhs, joinedChildren));
                return;
            }
        }

        parseTreeNodeStack.push(new ParseTreeNode(lhs, children));
    }
}