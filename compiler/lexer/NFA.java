package compiler.lexer;

import java.util.List;

public class NFA {
    public final List<NFAState> states;
    public final NFAState startState;
    public final List<NFAState> finalStates;

    public NFA(List<NFAState> states, NFAState startState, List<NFAState> finalStates){
        this.states = states;
        this.startState = startState;
        this.finalStates = finalStates;
    }
}
