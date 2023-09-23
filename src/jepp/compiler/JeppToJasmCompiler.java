package jepp.compiler;

import frontend.Token;
import frontend.lexer.Lexer;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import jepp.frontend.JePPFrontend;
import jepp.frontend.JeppParsePreprocessor;
import jepp.jevm.Instruction;
import jepp.jevm.Program;

import java.util.ArrayList;
import java.util.List;

public class JeppToJasmCompiler {
    public static Program compile(String input) {
        Lexer.Lex lex = JePPFrontend.tokenize(input);
        Parser.Parse parse = JePPFrontend.startParse();
        while(parse.process(lex.next()));

        JeppToJasmCompiler compiler = new JeppToJasmCompiler(parse.getParseTree());


        return null;
    }

    private final List<Instruction> res = new ArrayList<>();

    private JeppToJasmCompiler(ParseTreeNode parseTree) {
        createResult(JeppParsePreprocessor.process(parseTree));
    }

    private Program result() {
        return new Program(res.toArray(Instruction[]::new));
    }

    private void createResult(ParseTreeNode parseTree) {

    }
}