package jepp.jevm;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Program {
    private final Instruction[] instructions;
    public final int length;

    public Program(Instruction[] instructions){
        this.instructions = instructions;
        this.length = instructions.length;
    }

    public Instruction get(int i){
        if(i < 0 || i >= instructions.length) return Instruction.NOOP;
        return instructions[i];
    }

    @Override
    public String toString() {
        return IntStream.range(0, instructions.length).mapToObj(i -> i + ":\t " + instructions[i]).collect(Collectors.joining("\n"));
    }
}
