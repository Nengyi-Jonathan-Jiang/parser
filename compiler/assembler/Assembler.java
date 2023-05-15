package compiler.assembler;

import compiler.frontend.Symbol;
import compiler.frontend.SymbolTableReader;
import compiler.frontend.lexer.Lexer;
import compiler.frontend.parsers.LRParsers.LRParser;
import compiler.frontend.parsers.LRParsers.parsing_table.ParsingTable;
import compiler.frontend.parsers.Parser;

import java.util.Scanner;

public class Assembler {
    private static final String lexFile = "compiler/assembler/assembler.lx",
                                parseFile = "compiler/assembler/assembler.ebnf",
                                tableFile = "compiler/assembler/assembler.ptbl";
    private static final Symbol.SymbolTable symbolTable = Symbol.SymbolTable.merge(
        SymbolTableReader.generateFromLexerFile(lexFile),
        SymbolTableReader.generateFromLexerFile(parseFile)
    );
    private static final Lexer lex = Lexer.fromFile(symbolTable, lexFile);
    private static final Parser parser = new LRParser(ParsingTable.loadFromFile(symbolTable, tableFile));


}
