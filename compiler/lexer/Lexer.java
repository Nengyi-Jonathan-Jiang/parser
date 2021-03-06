package compiler.lexer;

import compiler.Token;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.regex.*;

public class Lexer {
    private List<TokenRule> tokenRules;

    public Lexer(){
        tokenRules = new LinkedList<>();
    }

    public void addRule(String name, String regex){
        addRule(name, regex, null);
    }
    public void addRule(String name, String regex, Function<Token, Token> func){
        tokenRules.add(new TokenRule(name, regex, func));
    }

    public void setFunc(String ruleName, Function<Token, Token> func){
        for(TokenRule tkr : tokenRules){
            if(tkr.name == ruleName){
                tkr.setFunc(func);
                return;
            }
        }
    }

    public static Lexer fromFile(String filename){
        Scanner scan;
        try{
            scan = new Scanner(new File(filename));
        }
        catch(Exception e){
            System.out.println("Could not read file!");
            return null;
        }
        Lexer lexer = new Lexer();
        while(scan.hasNextLine()){
            Scanner s = new Scanner(scan.nextLine());
            if(!s.hasNext()) continue;
            String name = s.next();
            if(!s.hasNext()){
                lexer.addRule(name, name.replaceAll("[#-.]|[\\[-^]|[?|{}]", "\\\\$0"));
                continue;
            }
            s.next();
            String regex = s.nextLine().stripLeading();
            lexer.addRule(name, regex);
        }
        return lexer;
    }
    
    private static class TokenRule{
        public String name;
        private Pattern regex;
        private Function<Token, Token> func;
        public TokenRule(String name, String regex, Function<Token, Token> func){
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
        private int index;
        private Lexer lexer;

        Lex(Lexer lexer, String input){
            this.lexer = lexer;
            s = input;
            index = 0;
        }

        public Token next(){
            if(s.length() == 0) return new Token("__END__","__END__", index);

            TokenRule rule = null;
            String sbstr = null;
            for(TokenRule tkr : lexer.tokenRules){
                int match = tkr.match(s);
                if(match != -1 && (sbstr == null || match > sbstr.length())){
                    rule = tkr;
                    sbstr = s.substring(0, match);
                }
            }

            if(rule != null && sbstr != null){
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