package compiler.grammar;

import java.io.*;
import java.util.*;
import compiler.Rule;

public class GrammarReader {
    private GrammarReader(){}
    
    private static Rule ruleFromLine(String str){
        Scanner scan = new Scanner(str);
        String lhs = scan.next();
        if(scan.next().equals("__EPSILON__")){
            scan.close();
            return new Rule(lhs);
        }
        List<String> rhs = new ArrayList<>();
        while(scan.hasNext()){
            rhs.add(scan.next());
        }
        scan.close();
        return new Rule(lhs, rhs);
    }
    private static Grammar readFromScanner(Scanner scan){
        String startSymbol = scan.next();
        List<Rule> rules = new ArrayList<>();
        while(scan.hasNextLine()){
            String line = scan.nextLine();
            if(line.length() == 0 || line.indexOf("//") == 0) continue; //Filter out empty or commented lines
            rules.add(ruleFromLine(line));
        }
        return new Grammar(rules, startSymbol);
    }

    public static Grammar readFromFile(String filename){
        Scanner scan;
        try{
            scan = new Scanner(new File(filename));
        }
        catch(Exception e){
            System.out.println("Could not read file!");
            return null;
        }
        Grammar res = readFromScanner(scan);
        scan.close();
        return res;
    }
}