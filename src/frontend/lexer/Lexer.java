package frontend.lexer;

import frontend.Symbol;
import frontend.Token;
import util.FileUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final Symbol.SymbolTable table;
    private final List<LexRule> tokenRules;

    public Lexer(Symbol.SymbolTable table) {
        this.table = table;
        tokenRules = new LinkedList<>();
    }

    public void addRule(Symbol name, String regex) {
        tokenRules.add(new LexRule(name, regex));
    }

    public void addIgnored(String regex) {
        tokenRules.add(new LexRule(null, regex));
    }

    public LexRule getRuleFor(Symbol name) {
        return tokenRules.stream().filter(i -> i.name == name).findFirst().orElse(null);
    }

    public static Lexer fromScanner(Symbol.SymbolTable symbolTable, Scanner scan) {
        Lexer lexer = new Lexer(symbolTable);
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
            throw new Error("Could not read file!");
        }
        return fromScanner(symbolTable, scan);
    }

    public static class LexRule {
        public final Symbol name;
        private final Pattern regex;

        public LexRule(Symbol name, String regex) {
            this.name = name;
            this.regex = Pattern.compile(regex);
        }

        public int match(String input) {
            Matcher m = regex.matcher(input);
            return m.find() && m.start() == 0 ? m.end() : -1;
        }
    }

    public Lex lex(String input) {
        return new Lex(this, input);
    }

    public static class Lex {
        private String s;
        private String[] parts;
        private int part_n = 0;
        private int index;
        private final Lexer lexer;

        Lex(Lexer lexer, String input) {
            this.lexer = lexer;

            var comment_rule = lexer.getRuleFor(lexer.table.get("COMMENT"));
            if (comment_rule != null) {
                parts = input.split(comment_rule.regex.pattern());
            } else parts = new String[]{input};

            s = "";
            index = 0;
        }

        public Token next() {
            while (s.length() == 0) {
                if (part_n >= parts.length) {
                    return new Token(lexer.table.__END__, "", index);
                }
                s = parts[part_n++];
            }

            LexRule rule = null;
            String sbstr = null;
            for (LexRule tkr : lexer.tokenRules) {
                int match = tkr.match(s);
                if (match != -1 && (sbstr == null || match > sbstr.length())) {
                    rule = tkr;
                    sbstr = s.substring(0, match);
                }
            }

            if (rule != null) {
                Token res = rule.name == null ? null : new Token(rule.name, sbstr, index);
                index += sbstr.length();
                s = s.substring(sbstr.length());

                if (res != null) {
                    return res;
                } else {
                    return next();
                }
            }
            index++;
            s = s.substring(1);
            return next();
        }
    }
}