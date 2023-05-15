package compiler.jevm;

public class VM {
    public final RAM ram = new RAM();
    private final Register.R1[] register1 = new Register.R1[8];
    private final Register.R4[] register4 = new Register.R4[8];
    private final Register.R4 stack_ptr = new Register.R4();
    private final Register.R4 instruction_ptr = new Register.R4();

    public void execute(Program program){
        instruction_ptr.setInt(0);
        while(instruction_ptr.getInt() != -1){
            var instruction = program.get(instruction_ptr.getInt());
            instruction.execute(this);
            instruction_ptr.setInt(instruction_ptr.getInt() + 1);
        }
    }

    public void jump(int location){
        instruction_ptr.setInt(location - 1);
    }

    private Register.R4 _getRegister4(int id){
        if(id == -1) return stack_ptr;
        if(id > 0 && id < register4.length) return register4[id];
        throw new IllegalAccessError("JeVM Error: Register index out of range (id=" + id + ")");
    }

    public MemoryLocation.M4 getR4(int id, boolean indirect){
        Register.R4 reg = _getRegister4(id);

        return indirect ? ram.getM4(reg.getInt()) : reg;
    }

    public MemoryLocation.M1 getR1(int id, boolean indirect){
        if(id != -1 || !indirect) // We can indirect the stack pointer to a 1 byte memory location
            if (id <= 0 || id >= register1.length) // Otherwise do OOB error checking on register ID
                throw new IllegalAccessError("JeVM Error: Register index out of range (id=" + id + ")");

        return indirect ? ram.getM1(_getRegister4(id).getInt()) : register1[id];
    }
}