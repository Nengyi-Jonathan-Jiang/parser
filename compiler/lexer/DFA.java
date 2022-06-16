package compiler.lexer;

import java.util.List;

public class DFA {
    public final List<DFAState> states;
    public final DFAState start;
    public final DFAState end;

    public DFA(List<DFAState> states, DFAState start, DFAState end){
        this.states = states;
        this.start = start;
        this.end = end;
    }

    public static DFA from(NFA automaton){
        // TODO: implement DFA.from(NFA)
        return null;
    }
}
