import frontend.parser.lr_parser.LRParsingError;
import jepp.frontend.JePPFrontend;
import jepp.interpreter.Interpreter;
import util.FileUtil;

import java.util.Scanner;

public class RunInterpreter {
    public static void main(String[] args) {
        if (args.length == 0) {
            runRepl();
        } else if (args.length == 1) {
            runProgram(args[0]);
        } else {
            System.out.println("Wrong number of arguments");
            System.out.println("Usage: java -jar jeInterp.jar [<optional-filename>]");
        }
    }

    private static void runProgram(String fileName) {
        String jeppProgram = FileUtil.getTextContents(fileName);
        System.out.println("Executing...");

        new Interpreter(System.in, System.out).run(jeppProgram);
    }

    private static void runRepl() {
        Interpreter interpreter = new Interpreter(System.in, System.out);

        // Load the parser and lexer
        JePPFrontend.beginLex("");
        JePPFrontend.beginParse();

        Scanner scan = new Scanner(System.in);
        StringBuilder currentInput = new StringBuilder("module main;");
        while (true) {
            System.out.print(">>> ");
            if (!scan.hasNextLine()) break;
            String line = scan.nextLine().trim();

            if(line.equals("quit")) {
                break;
            }

            if (line.endsWith("\\")) {
                currentInput.append(line, 0, line.length() - 1).append("\n");
                continue;
            }
            currentInput.append(line);

            try {
                interpreter.run(currentInput.toString());
            }
            catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
                for(Throwable error : e.getSuppressed()) {
                    if(error instanceof LRParsingError err) {
                        System.out.println(err.getMessage());
                    }
                }
            }

            currentInput = new StringBuilder("module main;");
        }
    }
}
