package frontend.lexer.fsm;

import frontend.Symbol;
import frontend.util.ComparableHashSet;
import frontend.util.ComparableSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DFA2NFA {
    private static class MANFA extends NFA {
        public Map<Integer, Symbol> acceptingStates = new HashMap<>();
    }

    private static ComparableSet<Integer> epsilon_closure(NFA nfa, Set<Integer> states) {
        ComparableSet<Integer> result = new ComparableHashSet<>(states);
        Queue<Integer> edge = new ArrayDeque<>(states);

        while(!edge.isEmpty()) {
            int currState = edge.poll();

            for(int equivalentState : nfa.table.getOrDefault(currState, new HashMap<>()).getOrDefault(NFA.EPSILON, new HashSet<>())) {
                if(result.contains(equivalentState)) continue;
                result.add(equivalentState);
                edge.offer(equivalentState);
            }
        }

        return result;
    }

    public static DFA to_dfa(Map<Symbol, NFA> rules){
        return createDFA(createMANFA(rules));
    }

    @NotNull
    private static DFA createDFA(MANFA manfa) {
        DFA dfa = new DFA();

        Map<ComparableSet<Integer>, Integer> dfaStates = new TreeMap<>();

        dfaStates.put(epsilon_closure(manfa, new HashSet<>(List.of(NFA.INITIAL_STATE))), DFA.INITIAL_STATE);

        boolean didUpdate = true;
        while (didUpdate) {
            didUpdate = false;

            for (var dfa_state : dfaStates.entrySet()) {
                ComparableSet<Integer> curr_NFA_states = dfa_state.getKey();
                Integer currStateID = dfaStates.get(curr_NFA_states);

                Map<Character, Set<Integer>> transitions = new HashMap<>();

                for(int state : curr_NFA_states) {
                    for(var entry : manfa.table.getOrDefault(state, new HashMap<>()).entrySet()) {
                        Character c = entry.getKey();
                        if(c != NFA.EPSILON) {
                            transitions.putIfAbsent(c, new HashSet<>());
                            transitions.get(c).addAll(entry.getValue());
                        }
                    }
                }

                for(var entry : transitions.entrySet()) {
                    ComparableSet<Integer> new_states = epsilon_closure(manfa, entry.getValue());

                    int newStateID;
                    if(dfaStates.containsKey(new_states)) {
                        newStateID = dfaStates.get(new_states);
                    }
                    else {
                        dfaStates.put(new_states, newStateID = DFA.getNextUnusedState());

                        new_states.stream().filter(manfa.acceptingStates::containsKey).findFirst().ifPresent(acceptedDFAState -> {
                            Symbol acceptedSymbol = manfa.acceptingStates.get(acceptedDFAState);
                            dfa.makeAcceptingState(acceptedSymbol, acceptedDFAState);
                        });

                        didUpdate = true;
                    }
                    dfa.addTransition(currStateID, entry.getKey(), newStateID);
                }
            }
        }
        return dfa;
    }

    @NotNull
    private static MANFA createMANFA(Map<Symbol, NFA> rules) {
        MANFA manfa = new MANFA();
        for(var entry : rules.entrySet()) {
            Symbol symbol = entry.getKey();
            NFA nfa = entry.getValue();
            int newAcceptingState = NFA.getNextUnusedState();
            nfa.remap(NFA.INITIAL_STATE, newAcceptingState);
            manfa.mergeWith(nfa);
            manfa.acceptingStates.put(newAcceptingState, symbol);
        }
        return manfa;
    }
}
