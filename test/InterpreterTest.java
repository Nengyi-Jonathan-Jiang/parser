import frontend.Token;
import frontend.lexer.Lexer;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import frontend.parser.lr_parser.LRParsingError;
import jepp.frontend.JePPFrontend;
import jepp.interpreter.Interpreter;
import org.junit.Assert;
import org.junit.Test;
import util.FileUtil;
import util.StringPrintStream;

import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class InterpreterTest {

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

    private void check(String directory){
        System.out.println("Reading file...");
        String program = FileUtil.getTextContents(directory + "/program.jepp");

        Lexer.Lex lex = JePPFrontend.beginLex(program);
        Parser.Parse parse = JePPFrontend.beginParse();
        System.out.println("Processing input...");
        Token tk;
        do {
            parse.process(tk = lex.next());
            System.out.print(tk + " ");
        } while (!tk.isEOF());

        System.out.println("Reading parse tree...");
        ParseTreeNode pTree = parse.getParseTree();

        InputStream input = FileUtil.getInputStream(directory + "/input.txt");
        StringPrintStream output = new StringPrintStream();
        String expected = FileUtil.getTextContents(directory + "/expected.txt");

        Interpreter interpreter = new Interpreter(input, output);
        interpreter.run(pTree);

        String sanitizedExpected = Arrays.stream(expected.strip().split("\n")).map(String::stripTrailing).collect(Collectors.joining("\n"));
        String sanitizedOutput = Arrays.stream(output.toString().strip().split("\n")).map(String::stripTrailing).collect(Collectors.joining("\n"));

        Assert.assertEquals(sanitizedExpected, sanitizedOutput);
    }
}
