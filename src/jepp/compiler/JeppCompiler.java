package jepp.compiler;

import frontend.lexer.Lexer;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import jepp.compiler.jeir.JeirNode;
import jepp.frontend.JePPFrontend;
import jepp.frontend.JeppParsePreprocessor;
import jepp.jevm.Instruction;
import jepp.jevm.Program;

import java.util.ArrayList;
import java.util.List;

public class JeppCompiler {
    public static Program compile(String input) {
        Lexer.Lex lex = JePPFrontend.beginLex(input);
        Parser.Parse parse = JePPFrontend.beginParse();
        while(parse.process(lex.next()));

        JeppCompiler compiler = new JeppCompiler(parse.getParseTree());


        return null;
    }

    private final List<Instruction> res = new ArrayList<>();

    private JeppCompiler(ParseTreeNode parseTree) {
        ParseTreeNode jeppParseTree = JeppParsePreprocessor.process(parseTree);
        JeirNode ir = createIR(jeppParseTree);
        createResult(ir);
    }

    private Program result() {
        return new Program(res.toArray(Instruction[]::new));
    }

    private JeirNode createIR(ParseTreeNode jeppParseNode) {
        switch (jeppParseNode.getDescription().name) {
            case "statements" -> {
                return new JeirNode.JeirStatements(
                    jeppParseNode.children().map(this::createIR).toArray(JeirNode[]::new)
                );
            }
            default -> {

            }
        }
        return null;
    }

    private void createResult(JeirNode parseTree) {

    }
}