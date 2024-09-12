import frontend.SymbolTableReader;
import frontend.grammar.ContextFreeGrammar;
import frontend.grammar.ContextFreeGrammarReader;
import frontend.parser.parser_generator.LR1ParseTableBuilder;
import jepp.assembler.Assembler;

public class BuildJasmParser {
    public static void main(String[] args){
        var symbolTable = SymbolTableReader.generateFromGrammarFile(Assembler.ebnfFile);

        System.out.println(symbolTable);

        ContextFreeGrammar grammar = ContextFreeGrammarReader.readFromFile(symbolTable, Assembler.ebnfFile);

        System.out.println("Generating Parser...");

        new LR1ParseTableBuilder(grammar).getTable().saveToFile("res/jasm/jasm.ptbl");
    }
}
