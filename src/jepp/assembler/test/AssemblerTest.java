package jepp.assembler.test;

import jepp.assembler.Assembler;
import jepp.jevm.VM;

public class AssemblerTest {
    public static void main(String[] args) {
        System.out.println("Compiling file...");
        var program = Assembler.assembleFile("test/fibonacci.jasm");
        System.out.println("Executing...");
        var vm = new VM();
        vm.execute(program);
    }
}
