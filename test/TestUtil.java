import org.junit.Assert;
import util.FileUtil;
import util.stringstream.StringPrintStream;

import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class TestUtil {
    private TestUtil() {
    }

    public interface ProgramRunner {
        void runProgram(String program, InputStream input, StringPrintStream output);
    }

    /**
     * Removes leading and trailing whitespace and the trailing whitespace after each line of the given string
     */
    private static String trimLines(String string) {
        return Arrays
            .stream(string.strip().split("\n"))
            .map(String::stripTrailing)
            .collect(Collectors.joining("\n"));
    }

    public static void testProgram(String inputFile, String expectedOutputFile, String programFile, ProgramRunner programRunner) {
        // Read files
        String programContents = FileUtil.getTextContents(programFile);
        String expectedOutput = FileUtil.getTextContents(expectedOutputFile);

        // Construct input and output streams
        InputStream input = FileUtil.getInputStream(inputFile);
        StringPrintStream output = new StringPrintStream();

        // Run the program
        programRunner.runProgram(programContents, input, output);

        // Check that output of program matches expected output
        String actualOutput = output.toString();
        Assert.assertEquals(trimLines(expectedOutput), trimLines(actualOutput));
    }
}
