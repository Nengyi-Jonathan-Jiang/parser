package compiler.assembler;

import compiler.frontend.Symbol;
import compiler.frontend.SymbolTableReader;
import compiler.frontend.Token;
import compiler.frontend.lexer.Lexer;
import compiler.frontend.parsers.LRParsers.LRParser;
import compiler.frontend.parsers.LRParsers.LRParsingError;
import compiler.frontend.parsers.LRParsers.parsing_table.ParsingTable;
import compiler.frontend.parsers.ParseTree;
import compiler.frontend.parsers.Parser;
import compiler.jevm.Program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Assembler {
    public static final String lxFile = "compiler/assembler/assembler.lx",
                               ebnfFile = "compiler/assembler/assembler.ebnf",
                               ptblFile = "compiler/assembler/assembler.ptbl";
    public static final Symbol.SymbolTable symbolTable = Symbol.SymbolTable.merge(
        SymbolTableReader.generateFromLexerFile(lxFile),
        SymbolTableReader.generateFromParsingTableFile(ptblFile)
    );
    public static final Lexer lexer = Lexer.fromFile(symbolTable, lxFile);
    public static final Parser parser = new LRParser(ParsingTable.loadFromFile(symbolTable, ptblFile));

    public static Program assemble(String program){
        var lex = lexer.lex(program);
        var parse = parser.start();

        Token tk;
        do {
            tk = lex.next();
            try {
                parse.process(tk);
            }
            catch (LRParsingError e) {
                System.out.print("Parse failed on token ");
                System.out.print(tk);
                System.out.print(" with stack [");
                System.out.print(((LRParser.Parse)parse).getParseTreeStack().stream().map(ParseTree::toString).collect(Collectors.joining(" ")));
                System.out.print("] : Expected one of ");
                System.out.println(e.getExpected());
                throw e;
            }
            catch (Error e) {
                System.out.println("Unexpected error while parsing");
                throw e;
            }
        } while(tk.type != symbolTable.__END__);

        var result = parse.getParseTree();
        if(result == null) throw new Error("ERROR PARSING STRING");

        // Flatten parse tree

        List<ParseTree> statements = new ArrayList<>();
        var s = result;
        while(s != null) {
            var children = s.getChildren();
            if(children.length > 0) {
                statements.add(0, children[1].getChildren()[0]);
                s = children[0];
            }
            else s = null;
        }

        System.out.println(statements.stream().map(ParseTree::prnt).collect(Collectors.joining("\n")));


        return null;
    }

    public static Program assembleFile(String file){
        try {
            return assemble(new String(Files.readAllBytes(Paths.get(file))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
