package frontend;

import java.util.Objects;
import java.util.Scanner;

public class SymbolTableReader {
    private SymbolTableReader(){}

    public static Symbol.SymbolTable generateFromParsingTableFile(String filename) {
        var table = new Symbol.SymbolTable();

        Scanner scan;
        try {
            scan = new Scanner(Objects.requireNonNull(SymbolTableReader.class.getResourceAsStream("/" + filename)));
        }
        catch(Exception e){
            throw new Error("Could not read file!");
        }

        int size = scan.nextInt();
        for(int state = 0; state < size; state++){
            String entryType;
            while(!(entryType = scan.next()).equals("s")){
                table.create(scan.next());
                switch (entryType) {
                    case "a" -> {
                        switch (scan.next()) {
                            case "a" -> {}
                            case "s" -> scan.nextInt();
                            case "r" -> {
                                for (int i = scan.nextInt(); i >= 0; i--)
                                    table.create(scan.next());
                            }
                        }
                    }
                    case "g" -> scan.nextInt();
                }
            }
        }

        table.lock();
        return table;
    }

    public static Symbol.SymbolTable generateFromGrammarFile(String filename) {
        var table = new Symbol.SymbolTable();

        Scanner sc;
        try {
            sc = new Scanner(SymbolTableReader.class.getResourceAsStream("/" + filename));
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

        table.lock();
        return table;
    }

    public static Symbol.SymbolTable generateFromLexerFile(String filename) {
        var table = new Symbol.SymbolTable();

        Scanner scan;
        try {
            scan = new Scanner(SymbolTableReader.class.getResourceAsStream("/" + filename));
        }
        catch(Exception e){
            throw new RuntimeException(e);
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
