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
import compiler.jevm.Instruction;
import compiler.jevm.Program;
import compiler.jevm.VM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Assembler {
    public static final String lxFile = "compiler/assembler/jasm.lx",
                               ebnfFile = "compiler/assembler/jasm.ebnf",
                               ptblFile = "compiler/assembler/jasm.ptbl";
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
            if(tk.type == symbolTable.get("COMMENT")) continue;
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

        // Second pass: calculate label locations
        Map<String, Integer> labels = new HashMap<>();
        {
            int instruction_number = 0;
            for (var i : statements) {
                if(i.getDescription() == symbolTable.get("label_declaration")) {
                    labels.put(i.getChildren()[1].getValue().value, instruction_number);
                }
                else instruction_number++;
            }
        }

        List<Instruction> instructions = new ArrayList<>();
        for(var i : statements) {
            if(i.getDescription() == symbolTable.get("label_declaration")) continue;

            instructions.add(switch(i.getDescription().toString()){
                case "dump" -> new Instruction() {
                    @Override
                    public void execute(VM jevm) {
                        jevm.ram.dumpContents();
                    }
                };
                case "display_statement" -> {
                    var param1 = createParam(i.getChildren()[1], labels);
                    yield new Instruction.DSP(param1);
                }
                case "print_statement" -> {
                    String str = i.getChildren()[1].getValue().value.replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t");
                    yield new Instruction.PRINT(str.substring(1, str.length() - 1));
                }
                case "input_statement" -> {
                    var param1 = createParam(i.getChildren()[1], labels);
                    yield new Instruction.INP(param1);
                }
                case "mov_statement" -> {
                    var param1 = createParam(i.getChildren()[1], labels);
                    var param2 = createParam(i.getChildren()[2], labels);
                    yield new Instruction.MOV(param1, param2);
                }
                case "arithmetic_statement" -> {
                    var type = i.getChildren()[0].getValue().value;
                    var param1 = createParam(i.getChildren()[1], labels);
                    var param2 = createParam(i.getChildren()[2], labels);
                    var dest = createParam(i.getChildren()[3], labels);
                    yield switch (type) {
                        case "add" -> new Instruction.ADD(param1, param2, dest);
                        case "sub" -> new Instruction.SUB(param1, param2, dest);
                        case "mul" -> new Instruction.MUL(param1, param2, dest);
                        case "div" -> new Instruction.DIV(param1, param2, dest);
                        case "mod" -> new Instruction.MOD(param1, param2, dest);
                        default -> Instruction.NOOP;
                    };
                }
                case "bitwise_statement" -> {
                    var type = i.getChildren()[0].getValue().value;
                    var param1 = createParam(i.getChildren()[1], labels);
                    var param2 = createParam(i.getChildren()[2], labels);
                    var dest = createParam(i.getChildren()[3], labels);
                    yield switch (type) {
                        case "and" -> new Instruction.AND(param1, param2, dest);
                        case "or" -> new Instruction.OR(param1, param2, dest);
                        case "xor" -> new Instruction.XOR(param1, param2, dest);
                        default -> Instruction.NOOP;
                    };
                }
                case "shift_statement" -> {
                    var type = i.getChildren()[0].getValue().value;
                    var param1 = createParam(i.getChildren()[1], labels);
                    var param2 = createParam(i.getChildren()[2], labels);
                    var dest = createParam(i.getChildren()[3], labels);
                    yield switch (type) {
                        case "shl" -> new Instruction.SHL(param1, param2, dest);
                        case "shr" -> new Instruction.SHR(param1, param2, dest);
                        default -> Instruction.NOOP;
                    };
                }
                case "jmp_statement" -> {
                    var param = createParam(i.getChildren()[1], labels);
                    var dest = createParam(i.getChildren()[2], labels);
                    byte cmp = switch(i.getChildren()[3].getValue().value) {
                        case "gtz" -> Instruction.JMP.GZ;
                        case "ltz" -> Instruction.JMP.LZ;
                        case "nez" -> Instruction.JMP.GZ | Instruction.JMP.LZ;
                        case "eqz" -> Instruction.JMP.EZ;
                        case "gez" -> Instruction.JMP.GZ | Instruction.JMP.EZ;
                        case "lez" -> Instruction.JMP.LZ | Instruction.JMP.EZ;
                        case "ucd" -> Instruction.JMP.GZ | Instruction.JMP.LZ | Instruction.JMP.EZ;
                        default -> throw new Error("");
                    };
                    yield new Instruction.JMP(param, dest, cmp);
                }
                default -> Instruction.NOOP;
            });
        }

        return new Program(instructions.toArray(Instruction[]::new));
    }

    private static Instruction.Param createParam(ParseTree tree, Map<String, Integer> label_locs) {
        return switch (tree.getDescription().toString()) {
            case "iconst" -> Instruction.Param.constant(Integer.parseInt(tree.getValue().value));
            case "fconst" -> Instruction.Param.constant(Float.parseFloat(tree.getValue().value));
            case "label_name" -> {
                try {
                    yield Instruction.Param.constant(label_locs.get(tree.getValue().value));
                }
                catch (NullPointerException e){
                    throw new Error("Could not find label " + tree.getValue().value);
                }
            }
            case "int_location" -> createRegister(Instruction.Param.ParamType.INT, tree.getChildren()[2]);
            case "float_location" -> createRegister(Instruction.Param.ParamType.FLOAT, tree.getChildren()[2]);
            default -> Instruction.Param.constant('?');
        };
    }

    private static Instruction.Param createRegister(Instruction.Param.ParamType type, ParseTree tree) {
        if(tree.getDescription() == symbolTable.get("reg")) {
            String s = tree.getValue().value.substring(1);
            int rId = s.equals("b") ? -2 : s.equals("p") ? -1 : s.charAt(0) - '0';
            return switch (type) {
                case INT, FLOAT -> Instruction.Param.reg(rId, type);
            };
        }
        else {
            String s = tree.getChildren()[1].getValue().value.substring(1);
            int rId = s.equals("b") ? -2 : s.equals("p") ? -1 : s.charAt(0) - '0';
            return switch (type) {
                case INT, FLOAT -> Instruction.Param.mem(rId, type);
            };
        }
    }

    public static Program assembleFile(String file){
        try {
            return assemble(new String(Files.readAllBytes(Paths.get(file))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}