package frontend.lexer;

import frontend.Location;
import frontend.Symbol;
import frontend.Token;
import util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexBasedLexer implements Lexer {
    private final Symbol.SymbolTable table;
    private final List<LexRule> tokenRules;

    public RegexBasedLexer(Symbol.SymbolTable table) {
        this.table = table;
        tokenRules = new ArrayList<>();
    }

    public void addRule(Symbol name, String regex) {
        tokenRules.add(new LexRule(name, regex));
    }

    public void addIgnored(String regex) {
        tokenRules.add(new LexRule(null, regex));
    }

    public static Lexer fromScanner(Symbol.SymbolTable symbolTable, Scanner scan) {
        RegexBasedLexer lexer = new RegexBasedLexer(symbolTable);
        while (scan.hasNextLine()) {
            Scanner s = new Scanner(scan.nextLine());
            if (!s.hasNext()) continue;
            String name = s.next();
            if (!s.hasNext()) {
                lexer.addRule(symbolTable.create(name), name.replaceAll("[#-.]|[\\[-^]|[?|{}]", "\\\\$0"));
                continue;
            }
            s.next();
            String regex = s.nextLine().stripLeading();
            lexer.addRule(symbolTable.create(name), regex);
        }
        return lexer;
    }

    public static Lexer fromFile(Symbol.SymbolTable symbolTable, String filename) {
        Scanner scan;
        try {
            scan = new Scanner(FileUtil.getInputStream(filename));
        } catch (Exception e) {
            throw new Error("Could not open file " + filename);
        }
        return fromScanner(symbolTable, scan);
    }

    private static class LexRule {
        public final Symbol name;
        private final Pattern regex;

        public LexRule(Symbol name, String regex) {
            this.name = name;
            this.regex = Pattern.compile("^" + regex);
        }

        public int match(String input, int start) {
            Matcher m = regex.matcher(input);
            m.region(start, input.length());
            return m.find() && m.start() == start ? m.end() - start : -1;
        }
    }

    @Override
    public Lex lex(String input) {
        return new RegexBasedLex(input);
    }

    private class RegexBasedLex implements Lex {
        private final String input;
        private int currentLine = 0;
        private int currentColumn = 0;
        private int currentIndex = 0;

        RegexBasedLex(String input) {
            this.input = input;
        }

        private void incrementLocationBy(int numCharacters) {
            for (int i = 0; i < numCharacters; i++) {
                switch (input.charAt(currentIndex)) {
                    case '\n' -> {
                        currentLine++;
                        currentColumn = 0;
                    }
                    case '\t' -> currentColumn += 4;
                    default -> currentColumn++;
                }
            }

            currentIndex += numCharacters;
        }

        @Override
        public Token next() {
            if (currentIndex == input.length()) {
                return new Token(
                    table.__END__,
                    "",
                    getCurrentLocation(),
                    getCurrentLocation()
                );
            }

            LexRule matchedRule = null;
            int matchedLength = 0;

            for (LexRule rule : tokenRules) {
                int currentLength = rule.match(input, currentIndex);
                if (currentLength > matchedLength) {
                    matchedRule = rule;
                    matchedLength = currentLength;
                }
            }

            if (matchedRule != null) {
                String matchedSubstring = input.substring(currentIndex, currentIndex + matchedLength);
                Location startLocation = getCurrentLocation();
                incrementLocationBy(matchedLength);
                Location endLocation = getCurrentLocation();

                if (matchedRule.name == null) {
                    return next();
                }

                return new Token(
                    matchedRule.name,
                    matchedSubstring,
                    startLocation,
                    endLocation
                );
            }

            incrementLocationBy(1);
            return next();
        }

        private Location getCurrentLocation() {
            return new Location(currentIndex, currentLine, currentColumn);
        }
    }
}