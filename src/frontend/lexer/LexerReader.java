package frontend.lexer;

import frontend.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Unfortunately, I have to write this by hand
public class LexerReader {
    public static Lexer readLexerFromString(String str) {
        Symbol.SymbolTable symbolTable = new Symbol.SymbolTable();
        List<Symbol> ignoredSymbols = new ArrayList<>();
        Lexer lexer = new Lexer(symbolTable);

        // Syntax:
        // lexer-file := [ lex-rule ]
        // lex-rule  := ignore regex
        //            | symbol-name := regex
        //            | single-word

        for (String line : str.trim().split("\\s*\n\\s*")) {
            line = line.trim();

            // Ignore comments and empty lines
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }

            // Try to process the line
            if (matchTokenDefinition(line, lexer, symbolTable)
                || matchIgnoredPattern(line, lexer)
                || matchSimpleRule(line, lexer, symbolTable)
            ) {
                continue;
            }

            // Illegal syntax
//            throw new RuntimeException("Unknown syntax while parsing lexer: " + line);
        }

        return lexer;
    }

    private static boolean matchSimpleRule(String line, Lexer lexer, Symbol.SymbolTable symbolTable) {
        if (line.matches("^\\S+$")) {
            String symbolName = line;
            String regex = line.replaceAll("[\\[.*+(\\-)?{|}^$\\\\\\]]", "\\\\$0");

            System.out.println("Symbol " + symbolName + " for pattern " + regex);

            lexer.addRule(symbolTable.create(symbolName), regex);
            return true;
        }
        return false;
    }

    private static boolean matchIgnoredPattern(String line, Lexer lexer) {
        Matcher matcher = Pattern.compile("^ignore\\s+(.*)$").matcher(line);
        if (matcher.find()) {
            String regex = matcher.group(1);

            System.out.println("Ignoring " + regex);

            lexer.addIgnored(regex);
            return true;
        }
        return false;
    }

    private static boolean matchTokenDefinition(String line, Lexer lexer, Symbol.SymbolTable symbolTable) {
        Matcher matcher = Pattern.compile("^(\"(?:[^\"\\\\\\s]|\\\\\\S)+\"|[\\w-]+)\\s+:=\\s+(.*)$").matcher(line);
        if (matcher.find()) {
            String symbolName = matcher.group(1);
            String regex = matcher.group(2);

            System.out.println("Symbol " + symbolName + " for pattern " + regex);

            lexer.addRule(symbolTable.create(symbolName), regex);

            return true;
        }
        return false;
    }
}
