package compiler.assembler.test;

import compiler.assembler.Assembler;
import compiler.jevm.VM;

public class AssemblerTest {
    public static void main(String[] args) {
        System.out.println("Compiling file...");
        var program = Assembler.assembleFile("compiler/assembler/test/fizzbuzz.jasm");
        System.out.println("Executing...");
        new VM().execute(program);
    }
}
