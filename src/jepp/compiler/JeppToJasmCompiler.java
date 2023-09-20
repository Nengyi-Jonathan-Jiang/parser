package jepp.compiler;

import frontend.Token;
import frontend.lexer.Lexer;
import frontend.parser.ParseTreeNode;
import frontend.parser.Parser;
import jepp.frontend.JePPFrontend;
import jepp.jevm.Instruction;
import jepp.jevm.Program;

import java.util.ArrayList;
import java.util.List;

public class JeppToJasmCompiler {
    public static Program compile(String input) {
        Lexer.Lex lex = JePPFrontend.tokenize(input);

        JeppToJasmCompiler compiler = new JeppToJasmCompiler();

        //noinspection StatementWithEmptyBody
        while(!compiler.acceptToken(lex.next())){}

        return null;
    }

    private final Parser.Parse parse = JePPFrontend.startParse();
    private final List<Instruction> res = new ArrayList<>();

    private boolean acceptToken(Token token) {
        parse.process(token);
        return token.isEOF();
    }

    private Program result() {
        createResult(parse.getParseTree());
        return new Program(res.toArray(Instruction[]::new));
    }

    private void createResult(ParseTreeNode parseTree) {

    }
}
