package frontend.lexer.regexEngine;

import frontend.Symbol;
import util.comparableSet.ComparableHashSet;
import util.comparableSet.ComparableSet;

import java.util.*;

public class DFA2NFA {
    private static class MultiAcceptNFA extends NFA {
        public Map<Integer, Symbol> acceptingStates = new HashMap<>();
    }

    private static ComparableSet<Integer> epsilon_closure(NFA nfa, Set<Integer> states) {
        ComparableSet<Integer> result = new ComparableHashSet<>(states);
        Queue<Integer> edge = new ArrayDeque<>(states);

        while (!edge.isEmpty()) {
            int currState = edge.poll();

            for (int equivalentState : nfa.table.getOrDefault(currState, new HashMap<>()).getOrDefault(NFA.EPSILON, new HashSet<>())) {
                if (result.contains(equivalentState)) continue;
                result.add(equivalentState);
                edge.offer(equivalentState);
            }
        }

        return result;
    }

    public static DFA to_dfa(Map<Symbol, NFA> rules) {
        return createDFA(createMANFA(rules));
    }


    private static DFA createDFA(MultiAcceptNFA multiAcceptNFA) {
        DFA dfa = new DFA();

        Map<ComparableSet<Integer>, Integer> dfaStates = new TreeMap<>();

        dfaStates.put(epsilon_closure(multiAcceptNFA, new HashSet<>(List.of(NFA.INITIAL_STATE))), DFA.INITIAL_STATE);

        boolean didUpdate = true;
        while (didUpdate) {
            didUpdate = false;

            for (var dfa_state : dfaStates.entrySet()) {
                ComparableSet<Integer> curr_NFA_states = dfa_state.getKey();
                Integer currStateID = dfaStates.get(curr_NFA_states);

                Map<Character, Set<Integer>> transitions = new HashMap<>();

                for (int state : curr_NFA_states) {
                    for (var entry : multiAcceptNFA.table.getOrDefault(state, new HashMap<>()).entrySet()) {
                        Character c = entry.getKey();
                        if (c != NFA.EPSILON) {
                            transitions.putIfAbsent(c, new HashSet<>());
                            transitions.get(c).addAll(entry.getValue());
                        }
                    }
                }

                for (var entry : transitions.entrySet()) {
                    ComparableSet<Integer> new_states = epsilon_closure(multiAcceptNFA, entry.getValue());

                    int newStateID;
                    if (dfaStates.containsKey(new_states)) {
                        newStateID = dfaStates.get(new_states);
                    } else {
                        dfaStates.put(new_states, newStateID = DFA.getNextUnusedState());

                        new_states.stream().filter(multiAcceptNFA.acceptingStates::containsKey).findFirst().ifPresent(acceptedDFAState -> {
                            Symbol acceptedSymbol = multiAcceptNFA.acceptingStates.get(acceptedDFAState);
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


    private static MultiAcceptNFA createMANFA(Map<Symbol, NFA> rules) {
        MultiAcceptNFA multiAcceptNFA = new MultiAcceptNFA();
        for (var entry : rules.entrySet()) {
            Symbol symbol = entry.getKey();
            NFA nfa = entry.getValue();
            int newAcceptingState = NFA.getNextUnusedState();
            nfa.remap(NFA.INITIAL_STATE, newAcceptingState);
            multiAcceptNFA.mergeWith(nfa);
            multiAcceptNFA.acceptingStates.put(newAcceptingState, symbol);
        }
        return multiAcceptNFA;
    }
}
