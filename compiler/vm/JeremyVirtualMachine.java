package compiler.vm;

public class JeremyVirtualMachine {
    private final RAM ram = new RAM();
    private final Register.Reg1[] register1 = new Register.Reg1[8];
    private final Register.Reg4[] register4 = new Register.Reg4[8];
    private final Register.Reg4 stack_ptr = new Register.Reg4();
    private final Register.Reg4 instruction_ptr = new Register.Reg4();


}