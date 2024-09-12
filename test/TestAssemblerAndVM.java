import jepp.assembler.Assembler;
import jepp.jevm.Program;
import jepp.jevm.VM;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.stringstream.StringPrintStream;

import java.io.InputStream;

// TODO: write all assembly programs for the rest of the test cases
public class TestAssemblerAndVM {
    @BeforeClass
    public static void setUp() {
        System.out.println("Loading assembler");
        Assembler.assemble("");
        System.out.println("Done.");
    }

    @Test
    public void testCompilerBasics() {
        check("test/jepp/basics");
    }

    @Test
    public void testCompilerRecursion() {
        check("test/jepp/recursion");
    }

    private void check(String directory) {
        String programFile = directory + "/program.jasm";
        String expectedOutputFile = directory + "/expected.txt";
        String inputFile = directory + "/input.txt";
        TestUtil.testProgram(inputFile, expectedOutputFile, programFile, this::runProgram);
    }

    private void runProgram(String program, InputStream input, StringPrintStream output) {
        Program assembledProgram = Assembler.assemble(program);
        VM virtualMachine = new VM(input, output);
        virtualMachine.execute(assembledProgram);
    }
}
