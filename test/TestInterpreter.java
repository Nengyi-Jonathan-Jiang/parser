import jepp.interpreter.Interpreter;
import org.junit.Test;
import util.stringstream.StringPrintStream;

import java.io.InputStream;

public class TestInterpreter {

    @Test
    public void testInterpreterBasics() {
        check("test/jepp/basics");
    }

    @Test
    public void testInterpreterVariables() {
        check("test/jepp/variables");
    }

    @Test
    public void testInterpreterFunctions() {
        check("test/jepp/functions");
    }

    @Test
    public void testInterpreterIfElse() {
        check("test/jepp/ifelse");
    }

    @Test
    public void testInterpreterRecursion() {
        check("test/jepp/recursion");
    }

    @Test
    public void testInterpreterLoops() {
        check("test/jepp/loops");
    }

    private void check(String directory) {
        String programFile = directory + "/program.jepp";
        String expectedOutputFile = directory + "/expected.txt";
        String inputFile = directory + "/input.txt";
        TestUtil.testProgram(inputFile, expectedOutputFile, programFile, this::runProgram);
    }

    private void runProgram(String program, InputStream input, StringPrintStream output) {
        Interpreter interpreter = new Interpreter(input, output);
        interpreter.run(program);
    }
}
