package compiler;

import compiler.grammar.Grammar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SymbolTableReader {
    private SymbolTableReader(){}

    public static Symbol.SymbolTable generateFromGrammarFile(String filename) {
        var table = new Symbol.SymbolTable();

        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        }
        catch(Exception e){
            throw new Error("Could not read file!");
        }

        table.create(sc.next());

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            if(line.length() == 0 || line.indexOf("//") == 0) continue; //Filter out empty or commented lines


            Scanner scan = new Scanner(line);

            table.create(scan.next());
            if(scan.next().equals("__EPSILON__")) continue;
            while(scan.hasNext()) table.create(scan.next());
        }

        return table;
    }

    public static Symbol.SymbolTable generateFromLexerFile(String filename) {
        var table = new Symbol.SymbolTable();

        Scanner scan;
        try {
            scan = new Scanner(new File(filename));
        }
        catch(Exception e){
            throw new Error("Could not read file!");
        }
        while(scan.hasNextLine()){
            Scanner s = new Scanner(scan.nextLine());
            if(!s.hasNext()) continue;
            String name = s.next();
            table.create(name);
        }
        return table;
    }
}
