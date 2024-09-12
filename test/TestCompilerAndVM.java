import jepp.compiler.JeppCompiler;
import jepp.jevm.Program;
import jepp.jevm.VM;
import org.junit.Test;
import util.stringstream.StringPrintStream;

import java.io.InputStream;

public class TestCompilerAndVM {

    @Test
    public void testCompilerBasics() {
        check("test/jepp/basics");
    }

    @Test
    public void testCompilerVariables() {
        check("test/jepp/variables");
    }

    @Test
    public void testCompilerFunctions() {
        check("test/jepp/functions");
    }

    @Test
    public void testCompilerIfElse() {
        check("test/jepp/ifelse");
    }

    @Test
    public void testCompilerRecursion() {
        check("test/jepp/recursion");
    }

    @Test
    public void testCompilerLoops() {
        check("test/jepp/loops");
    }

    private void check(String directory) {
        String programFile = directory + "/program.jepp";
        String expectedOutputFile = directory + "/expected.txt";
        String inputFile = directory + "/input.txt";
        TestUtil.testProgram(inputFile, expectedOutputFile, programFile, this::runProgram);
    }

    private void runProgram(String program, InputStream input, StringPrintStream output) {
        Program compiledProgram = JeppCompiler.compile(program);
        VM virtualMachine = new VM(input, output);
        virtualMachine.execute(compiledProgram);
    }
}
