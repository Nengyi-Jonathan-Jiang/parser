package frontend.parser.lr_parser.parsing_table;

import frontend.Symbol;
import frontend.Token;
import frontend.parser.ParseTreeNode;
import frontend.parser.Rule;

import java.util.Objects;
import java.util.Stack;

public record ReduceEntry(Rule rule) implements ActionTableEntry {

    public void writeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append(" r ");

        stringBuilder.append(rule.getRhsSize());
        stringBuilder.append(" ");

        if (rule.chained) stringBuilder.append("__c ");
        if (!rule.unwrapMono) stringBuilder.append("__w ");

        stringBuilder.append(rule.getLhs());
        stringBuilder.append(" ");
        stringBuilder.append(rule.getRhs().toString());
    }

    public void applyAction(Stack<Integer> stateStack, Stack<ParseTreeNode> parseTreeNodeStack, ParsingTable table, Token token) {
        Symbol lhs = rule.getLhs();

        // Update state stack
        for (int j = 0; j < rule.getRhsSize(); j++) stateStack.pop();
        GotoEntry gotoEntry = table.getGoto(Objects.requireNonNull(stateStack.peek()), lhs);
        stateStack.push(gotoEntry.nextState());

        // Update parse tree
        ParseTreeNode[] children = new ParseTreeNode[rule.getRhsSize()];
        for (int j = rule.getRhsSize() - 1; j >= 0; j--) {
            children[j] = parseTreeNodeStack.pop();
        }

        parseTreeNodeStack.push(rule.applyTo(children));
    }

    @Override
    public boolean isDoneProcessingToken() {
        return false;
    }

    @Override
    public boolean isAccepting() {
        return false;
    }
}