package frontend.grammar;

import frontend.parser.Rule;
import frontend.Symbol;

import java.util.*;

public class GrammarReader {
    private GrammarReader(){}
    
    private static Rule ruleFromLine(Symbol.SymbolTable table, String str){
        Scanner scan = new Scanner(str);
        boolean unwrap = true, chained = false;
        String lhs;
        {
            String n = scan.next();
            while(true) {
                if(n.equals("__NO_UNWRAP__"))  unwrap = false;
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
    private static Grammar readFromScanner(Symbol.SymbolTable table, Scanner scan){
        Symbol startSymbol = table.get(scan.next());
        List<Rule> rules = new ArrayList<>();
        while(scan.hasNextLine()){
            String line = scan.nextLine();
            if(line.length() == 0 || line.indexOf("//") == 0) continue; //Filter out empty or commented lines
            rules.add(ruleFromLine(table, line));
        }
        return new Grammar(rules, startSymbol, table);
    }

    public static Grammar readFromFile(Symbol.SymbolTable table, String filename){
        Scanner scan;
        try{
            scan = new Scanner(GrammarReader.class.getResourceAsStream("/" + filename));
        }
        catch(Exception e){
            System.out.println("Could not read file!");
            return null;
        }
        Grammar res = readFromScanner(table, scan);
        scan.close();
        return res;
    }
}