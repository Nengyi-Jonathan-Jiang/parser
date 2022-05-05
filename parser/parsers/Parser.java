package parser.parsers;

import parser.parsingTable.*;
import parser.Rule;
import java.util.*;

public class Parser {
    private ParsingTable table;

    public Parser(ParsingTable table){
        this.table = table;
    }

    public ParseTree parse(String[] tokens){
        String[] tkns = new String[tokens.length + 1];
        System.arraycopy(tokens, 0, tkns, 0, tokens.length);
        tkns[tokens.length] = "__END__";

        Deque<Integer> stateStack = new ArrayDeque<>();
        Deque<ParseTree> tknStack = new ArrayDeque<>();
        stateStack.push(0);

        int index = 0;
        while(index < tkns.length){
            int state = stateStack.peek();
            String tkn = tkns[index];

            TableEntry entry = table.getAction(state, tkn);

            if(entry == null){
                System.out.println("Could not parse string!");
                return null;
            }
            
            System.out.print(String.format("%-20s", tknStack.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ")));

            switch(entry.getAction()){
                case SHIFT:
                    stateStack.push(((ShiftEntry)entry).getNextState());
                    System.out.println("SHIFT \"" + tkn + "\"");
                    tknStack.push(new ParseTree(tkn));
                    index++;
                    break;
                case ACCEPT:
                    System.out.println("ACCEPTED INPUT");
                    return null;
                case REDUCE:
                    Rule reduceRule = ((ReduceEntry)entry).getRule();
                    System.out.println("REDUCE " + reduceRule);
                    String lhs = reduceRule.getLhs();
                    ParseTree[] children = new ParseTree[reduceRule.getRhsSize()];
                    for(int j = 0; j < reduceRule.getRhsSize(); j++){
                        stateStack.pop();
                        children[j] = tknStack.pop();
                    }
                    GotoEntry gotoEntry = (GotoEntry)table.getGoto(stateStack.peek(), lhs);
                    stateStack.push(gotoEntry.getNextState());
                    tknStack.push(new ParseTree(lhs, children));
                    break;
                case GOTO: break;
            }
        }
        return tknStack.getFirst();
    }
}
