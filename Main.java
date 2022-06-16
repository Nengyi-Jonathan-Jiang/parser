import compiler.parsers.*;
import compiler.parsing_table.ParsingTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		System.out.println("Generating Parser...");
		LRParser parse = new LRParser(ParsingTable.loadFromFile("Code.ptbl"));

		System.out.println("Parsing...");

		ArrayList<String> tkns = new ArrayList<>();

		{
			Scanner scan;
			try {
				scan = new Scanner(new File("TokenStream.txt"));
			} catch (Exception e) {
				System.out.println("Could not read file TokenStream.txt");
				return;
			}
			while (scan.hasNext()) {
				tkns.add(scan.next());
			}
			scan.close();
		}

		ParseTree pTree = parse.parse(tkns.toArray(String[]::new));
		if(pTree == null) System.out.println("ERROR PARSING STRING");
		else System.out.println(pTree.prnt());
	}
}