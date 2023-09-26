import jepp.compiler.JeppCompiler;
import jepp.jevm.Program;
import jepp.jevm.VM;
import org.junit.Assert;
import org.junit.Test;
import util.FileUtil;
import util.StringPrintStream;

import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CompilerTest {

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

    private void check(String directory){
        System.out.println("Reading file...");
        String program = FileUtil.getTextContents(directory + "/program.jepp");

        Program compiledProgram = JeppCompiler.compile(program);

        InputStream input = FileUtil.getInputStream(directory + "/input.txt");
        StringPrintStream output = new StringPrintStream();
        String expected = FileUtil.getTextContents(directory + "/expected.txt");

        VM virtualMachine = new VM(input, output);
        virtualMachine.execute(compiledProgram);

        String sanitizedExpected = Arrays.stream(expected.strip().split("\n")).map(String::stripTrailing).collect(Collectors.joining("\n"));
        String sanitizedOutput = Arrays.stream(output.toString().strip().split("\n")).map(String::stripTrailing).collect(Collectors.joining("\n"));

        Assert.assertEquals(sanitizedExpected, sanitizedOutput);
    }
}
