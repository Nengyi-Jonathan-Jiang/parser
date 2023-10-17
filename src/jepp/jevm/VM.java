package jepp.jevm;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class VM {
    public final RAM ram = new RAM();
    private final Register[] registers = new Register[10];
    private final PrintStream output;
    private int instruction_ptr;
    public final Allocator allocator = new Allocator();
    public final Scanner scan;

    public VM(InputStream input, PrintStream output) {
        this.output = output;
        for(int i = 0; i < registers.length; i++) registers[i] = new Register();
        scan = new Scanner(input);
    }

    public void execute(Program program){
        instruction_ptr = 0;
        int i = 0;
        while(instruction_ptr != -1 && instruction_ptr < program.length){
            var instruction = program.get(instruction_ptr);
            instruction.execute(this);
            instruction_ptr++;
            if(++i > 1e9) throw new JeVMError("TLE (Executed more than 1e9 instructions)");
        }
    }

    public void jump(int location){
        instruction_ptr = location - 1;
    }

    public Register getRegister(int id){
        try {
            return registers[id + 2];
        }
        catch(IndexOutOfBoundsException e){
            throw new JeVMError("Register index out of range (id=" + id + ")");
        }
    }

    public MemoryLocation getMemory(int id){
        return ram.get(getRegister(id).getInt());
    }

    public void display(String s){
        // TODO: add log file?
        output.print(s);
    }

    public int readInt() {
        return scan.nextInt();
    }

    public float readFloat() {
        return scan.nextFloat();
    }
}