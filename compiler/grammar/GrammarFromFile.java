package compiler.grammar;

import java.io.*;
import java.util.*;
import compiler.Rule;

public class GrammarFromFile {
    private static Rule ruleFromLine(String str){
        Scanner scan = new Scanner(str);
        String lhs = scan.next();
        scan.next();    //There must be a separator between lhs and rhs but it can be any token
        List<String> rhs = new ArrayList<>();
        while(scan.hasNext()) rhs.add(scan.next());
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

    public static Grammar readFromString(String grammar){
        Scanner scan = new Scanner(grammar);
        Grammar res = readFromScanner(scan);
        scan.close();
        return res;
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