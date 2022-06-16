package compiler.lexer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lexer {
    public static void main(String[] args){
        String reg = "acbdegfh";
        DFA dfa = Lexer.toDFA(reg);

        System.out.println(dfa.start);
        for(DFAState state : dfa.states){
            System.out.println(state);
        }
        System.out.println(dfa.end);
    }

    public Lexer(DFA[] rules){

    }

    private static DFA toDFA(String regex){
        regex = regex
                .replace("\\\\", "\\")
                .replace("\\d", "0123456789")
                .replace("\\w", "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz")
                .replace("\\n", "\n");
        DFAState startState = new DFAState();
        return toDFA(new CharacterIterator(regex), startState);
    }

    private static DFA toDFA(CharacterIterator iterator, DFAState start){

        char chr = iterator.next();

        DFA dfa;

        switch(chr){
            case '|':
                return null;
            case '\\':
                dfa = new DFA(null, null, null);
                break;
            case '(':
                dfa = new DFA(null, null, null);
                break;
            case '[':
                dfa = new DFA(null, null, null);
                break;
            default:
                DFAState end = new DFAState();
                start.on(chr, end);
                dfa = new DFA(new ArrayList<>(), start, end);
                break;
        }

        if(iterator.hasNext()){
            DFA after = toDFA(iterator, dfa.end);
            if(after != null) {
                return new DFA(Stream.concat(Stream.of(dfa.end), after.states.stream()).collect(Collectors.toList()), start, after.end);
            }
        }
        return dfa;
    }
}

class CharacterIterator implements Iterator<Character> {
    private final String str;
    private int pos = 0;

    public CharacterIterator(String str) {
        this.str = str;
    }
    public boolean hasNext() {
        return pos < str.length();
    }
    public Character next() {
        return str.charAt(pos++);
    }

    public Character peek() {
        return str.charAt(pos);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}