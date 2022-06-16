package compiler.lexer;

import java.util.*;

public class NFAState implements Iterable<Map.Entry<Character,List<NFAState>>>{
    private final Map<Character, List<NFAState>> transitions;

    public NFAState(){
        this(new TreeMap<>());
    }

    public NFAState(Map<Character, List<NFAState>> transitions){
        this.transitions = transitions;
    }

    public void on(char chr, NFAState state){
        if (transitions.containsKey(chr)) {
            this.on(chr).add(state);
        }
        else {
            transitions.put(chr, Collections.singletonList(state));
        }
    }

    public List<NFAState> on(char chr){
        return transitions.get(chr);
    }

    public NFAState merge(NFAState other){
        Map<Character, List<NFAState>> merged = new TreeMap<>();

        for(var transition : this){
            merged.put(transition.getKey(), transition.getValue());
        }

        for(var transition : other){
            Character chr = transition.getKey();
            List<NFAState> states = transition.getValue();
            if(merged.containsKey(chr)){
                merged.get(chr).addAll(states);
            }
            else merged.put(chr, states);
        }
        return new NFAState(merged);
    }

    @Override
    public Iterator<Map.Entry<Character, List<NFAState>>> iterator() {
        return transitions.entrySet().iterator();
    }
}
