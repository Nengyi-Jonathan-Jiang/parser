package frontend.grammar;

import frontend.Symbol;
import frontend.parser.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ContextFreeGrammarReader {
    private ContextFreeGrammarReader(){}
    
    private static Rule ruleFromLine(Symbol.SymbolTable table, String str){
        Scanner scan = new Scanner(str);
        boolean unwrap = true, chained = false;
        String lhs;
        {
            String n = scan.next();
            while(true) {
                if(n.equals("__WRAP__"))  unwrap = false;
                else if(n.equals("__CHAIN__")) chained = true;
                else break;

                n = scan.next();
            }
            lhs = n;
        }
        if(scan.next().equals("__EPSILON__")){
            scan.close();
            return new Rule(table.get(lhs), false, true);
        }
        List<Symbol> rhs = new ArrayList<>();

        while(scan.hasNext()) rhs.add(table.get(scan.next()));

        scan.close();
        return new Rule(table.get(lhs), unwrap, chained, rhs);
    }

    private static ContextFreeGrammar readFromScanner(Symbol.SymbolTable table, Scanner scan){
        Symbol startSymbol = table.get(scan.next());
        List<Rule> rules = new ArrayList<>();
        while(scan.hasNextLine()){
            String line = scan.nextLine();
            if(line.isEmpty() || line.indexOf("//") == 0) continue; //Filter out empty or commented lines
            rules.add(ruleFromLine(table, line));
        }
        return new ContextFreeGrammar(rules, startSymbol, table);
    }

    public static ContextFreeGrammar readFromFile(Symbol.SymbolTable table, String filename){
        Scanner scan;
        try{
            scan = new Scanner(Objects.requireNonNull(ContextFreeGrammarReader.class.getResourceAsStream("/" + filename)));
        }
        catch(Exception e){
            throw new RuntimeException("Could not read grammar file");
        }
        ContextFreeGrammar res = readFromScanner(table, scan);
        scan.close();
        return res;
    }
}