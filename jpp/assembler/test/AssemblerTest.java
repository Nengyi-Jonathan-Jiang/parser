package jpp.assembler.test;

import jpp.assembler.Assembler;
import jpp.jevm.VM;

public class AssemblerTest {
    public static void main(String[] args) {
        System.out.println("Compiling file...");
        var program = Assembler.assembleFile("compiler/assembler/test/fizzbuzz.jasm");
        System.out.println("Executing...");
        var vm = new VM();
        vm.execute(program);
    }
}
