package parser;
import java.util.*;

import parser.parsingTable.*;

public class LRParser {
    private ParsingTable table;
    public LRParser(ParsingTable table){
        this.table = table;
    }
    public void parse(String[] tokens){
        String[] tkns = new String[tokens.length + 1];
        System.arraycopy(tokens, 0, tkns, 0, tokens.length);
        tkns[tokens.length] = "__END__";

        Deque<Integer> stateStack = new ArrayDeque<>();
        Deque<String> tknStack = new ArrayDeque<>();
        stateStack.push(0);

        for(int i = 0; i < tkns.length; ){
            int state = stateStack.peek();
            String tkn = tkns[i];

            // System.out.println("Current state: " + state);
            // System.out.println("Current token: \"" + tkn + "\"");
            // System.out.println(stateStack);

            TableEntry entry = table.getAction(state, tkn);

            if(entry == null){
                System.out.println("Could not parse string!");
                return;
            }

            switch(entry.getAction()){
                case SHIFT:
                    stateStack.push(((ShiftEntry)entry).getNextState());
                    System.out.println("SHIFT \"" + tkn + "\"");
                    tknStack.push(tkn);
                    i++;
                    break;
                case ACCEPT:
                    System.out.println("ACCEPTED INPUT");
                    return;
                case REDUCE:
                    Rule reduceRule = ((ReduceEntry)entry).getRule();
                    System.out.println("REDUCE " + reduceRule);
                    String lhs = reduceRule.getLhs();
                    
                    for(int j = 0; j < reduceRule.getRhs().size(); j++){
                        stateStack.pop();
                        tknStack.pop();
                    }

                    // System.out.println(stateStack);

                    // System.out.println("Querying GOTO[" + state + "][\"" + lhs + "\"]");

                    GotoEntry gotoEntry = (GotoEntry)table.getGoto(stateStack.peek(), lhs);
                    stateStack.push(gotoEntry.getNextState());
                    tknStack.push(lhs);
                    break;
                case GOTO: break;
            }

            
            System.out.print(String.format("%-20s", tknStack.toString().replaceAll("^\\[|\\]$", "").replaceAll(", ", " ")));
        }
    }
}
