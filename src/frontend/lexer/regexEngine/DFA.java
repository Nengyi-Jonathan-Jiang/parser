package frontend.lexer.regexEngine;

import frontend.Symbol;

import java.util.HashMap;
import java.util.Map;

public class DFA {
    public Map<Integer, Map<Character, Integer>> table = new HashMap<>();

    public Map<Integer, Symbol> acceptingStates = new HashMap<>();
    
    public static final int INITIAL_STATE = 0;

    private static int nextState = 1;

    public static int getNextUnusedState() {
        return nextState++;
    }

    public DFA() {}

    public void addTransition(int currState, char character, int nextState) {
        table.putIfAbsent(currState, new HashMap<>());
        Map<Character, Integer> entry = table.get(currState);

        entry.put(character, nextState);
    }

    public void makeAcceptingState(Symbol symbol, int state) {
        acceptingStates.put(state, symbol);
    }

    public void print() {
        for(var entry : table.entrySet()) {
            int state = entry.getKey();
            for(var entry2 : entry.getValue().entrySet()) {
                char c = entry2.getKey();
                Integer state2 = entry2.getValue();
                System.out.println(state + " --" + c + "-> " + state2 + (
                    acceptingStates.containsKey(state2) ?
                        " accepts " + acceptingStates.get(state2)
                        : ""
                ));
            }
        }
    }

    public static class DFARunner {
        private static final int ERROR_STATE = -1;

        private final DFA dfa;
        private int currState = DFA.INITIAL_STATE;

        public DFARunner(DFA dfa) {
            this.dfa = dfa;
        }

        void process(char c) {
            if(currState == ERROR_STATE) return;
            if(!dfa.table.get(currState).containsKey(c)) currState = ERROR_STATE;
            currState = dfa.table.get(currState).get(c);
        }

        boolean isInErrorState() {
            return currState == ERROR_STATE;
        }

        Symbol getAcceptedSymbol() {
            return dfa.acceptingStates.get(currState);
        }
    }
}