package frontend.lexer.fsm;

import java.util.*;

public class NFA {
    public final Map<Integer, Map<Character, Set<Integer>>> table = new TreeMap<>();

    public static final int INITIAL_STATE = 0;
    public static final int ACCEPTING_STATE = 1;
    public static final char EPSILON = 'ε';

    private static int nextState = 2;

    public static int getNextUnusedState() {
        return nextState++;
    }

    public NFA() {}

    public void addTransition(int currState, char character, int nextState) {
        table.putIfAbsent(currState, new HashMap<>());
        Map<Character, Set<Integer>> entry = table.get(currState);

        entry.putIfAbsent(character, new HashSet<>());

        // Remember, we don't allow multiple normal transitions, only epsilon transitions
        assert character == EPSILON || (entry.get(character).isEmpty());

        entry.get(character).add(nextState);
    }

    // This method will be highly useful when merging NFAs -- we'll get to that soon!
    public void remap(int newInitialState, int newAcceptingState) {
        // We can't do anything to an empty table!
        if(table.isEmpty()) return;

        // Re-route all transitions to NFA.INITIAL_STATE to newInitialState
        // and all transitions to NFA.ACCEPTING_STATE to newAcceptingState

        for (Map<Character, Set<Integer>> entry : table.values()) {
            for (Set<Integer> targets : entry.values()) {
                if (targets.contains(NFA.INITIAL_STATE)) {
                    targets.remove(NFA.INITIAL_STATE);
                    targets.add(newInitialState);
                }
                if (targets.contains(NFA.ACCEPTING_STATE)) {
                    targets.remove(NFA.ACCEPTING_STATE);
                    targets.add(newAcceptingState);
                }
            }
        }
        // Finally move all the outgoing transitions from NFA.INITIAL_STATE to
        // newInitialState
        Map<Character, Set<Integer>> transitions = table.get(NFA.INITIAL_STATE);
        table.remove(NFA.INITIAL_STATE);
        table.put(newInitialState, transitions);
    }

    // Directly merge with another NFA without remapping states
    // This method assumes that the other NFA has already been remapped and shares
    // no common states with this NFA.
    public void mergeWith(NFA other) {
        table.putAll(other.table);
    }

    public void print() {
        for(var entry : table.entrySet()) {
            int state = entry.getKey();
            for(var entry2 : entry.getValue().entrySet()) {
                char c = entry2.getKey();
                System.out.println(state + " --" + c + "-> " + entry2.getValue());
            }
        }
    }

    // Don't worry about how to run the NFA for now -- it's not very important, as we will optimize
    // it to a DFA anyway
}