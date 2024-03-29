package frontend.lexer;

import frontend.Symbol;
import frontend.Token;

import java.util.*;
import java.util.function.Function;
import java.util.regex.*;

public class Lexer {
    private final Symbol.SymbolTable table;
    private final List<TokenRule> tokenRules;

    public Lexer(Symbol.SymbolTable table){
        this.table = table;
        tokenRules = new LinkedList<>();
    }

    public void addRule(Symbol name, String regex){
        addRule(name, regex, null);
    }
    public void addRule(Symbol name, String regex, Function<Token, Token> func){
        tokenRules.add(new TokenRule(name, regex, func));
    }

    public TokenRule getRuleFor(Symbol name) {
        return tokenRules.stream().filter(i -> i.name == name).findFirst().orElse(null);
    }

    public static Lexer fromScanner(Symbol.SymbolTable symbolTable, Scanner scan){
        Lexer lexer = new Lexer(symbolTable);
        while(scan.hasNextLine()){
            Scanner s = new Scanner(scan.nextLine());
            if(!s.hasNext()) continue;
            String name = s.next();
            if(!s.hasNext()){
                lexer.addRule(symbolTable.create(name), name.replaceAll("[#-.]|[\\[-^]|[?|{}]", "\\\\$0"));
                continue;
            }
            s.next();
            String regex = s.nextLine().stripLeading();
            lexer.addRule(symbolTable.create(name), regex);
        }
        return lexer;
    }

    public static Lexer fromFile(Symbol.SymbolTable symbolTable, String filename){
        Scanner scan;
        try {
            scan = new Scanner(Lexer.class.getResourceAsStream("/" + filename));
        }
        catch(Exception e){
            throw new Error("Could not read file!");
        }
        return fromScanner(symbolTable, scan);
    }
    
    private static class TokenRule{
        public final Symbol name;
        private final Pattern regex;
        private Function<Token, Token> func;
        public TokenRule(Symbol name, String regex, Function<Token, Token> func){
            this.name = name;
            this.regex = Pattern.compile(regex);
            this.func = func;
        }

        public int match(String input){
            Matcher m = regex.matcher(input);
            return m.find() && m.start() == 0 ? m.end() : -1;
        }

        public Token func(Token token){
            return func == null ? token : this.func.apply(token);
        }

        public void setFunc(Function<Token, Token> func){
            this.func = func;
        }
    }

    public Lex lex(String input){
        return new Lex(this, input);
    }

    public static class Lex{
        private String s;
        private String[] parts;
        private int part_n = 0;
        private int index;
        private final Lexer lexer;

        Lex(Lexer lexer, String input){
            this.lexer = lexer;

            var comment_rule = lexer.getRuleFor(lexer.table.get("COMMENT"));
            if(comment_rule != null) {
                parts = input.split(comment_rule.regex.pattern());
            }
            else parts = new String[]{input};

            s = "";
            index = 0;
        }

        public Token next(){
            while (s.length() == 0){
                if(part_n >= parts.length) {
                    return new Token(lexer.table.__END__, "", index);
                }
                s = parts[part_n++];
            }

            TokenRule rule = null;
            String sbstr = null;
            for(TokenRule tkr : lexer.tokenRules){
                int match = tkr.match(s);
                if(match != -1 && (sbstr == null || match > sbstr.length())){
                    rule = tkr;
                    sbstr = s.substring(0, match);
                }
            }

            if(rule != null) {
                Token res = new Token(rule.name, sbstr, index);
                index += sbstr.length();
                s = s.substring(sbstr.length());
                return rule.func(res);
            }
            index++;
            s = s.substring(1);
            return next();
        }
    }
}