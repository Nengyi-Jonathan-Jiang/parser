package compiler.lexer;

import java.util.*;
import java.util.stream.Collectors;

public class DFAState implements Iterable<Map.Entry<Character, DFAState>>{
    private static int s_id = 0;
    private final int id;

    private final Map<Character, DFAState> transitions;

    public DFAState(){
        this(new TreeMap<>());
    }

    public DFAState(Map<Character, DFAState> transitions){
        this.id = ++s_id;
        this.transitions = transitions;
    }

    public void on(char chr, DFAState state){
        transitions.put(chr, state);
    }

    public DFAState on(char chr){
        return transitions.get(chr);
    }

    public String toString(){
        return "State<" + id + ">{" + transitions.entrySet().stream().map(i->
            "'" + i.getKey() + "' -> " + i.getValue().id
        ).collect(Collectors.joining(", ")) + "}";
    }

    @Override
    public Iterator<Map.Entry<Character, DFAState>> iterator() {
        return transitions.entrySet().iterator();
    }
}
