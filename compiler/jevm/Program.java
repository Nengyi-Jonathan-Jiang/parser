package compiler.jevm;

public class Program {
    private final Instruction[] instructions;

    Program(Instruction[] instructions){
        this.instructions = instructions;
    }

    Instruction get(int i){
        if(i < 0 || i >= instructions.length) return Instruction.NOOP;
        return instructions[i];
    }
}
