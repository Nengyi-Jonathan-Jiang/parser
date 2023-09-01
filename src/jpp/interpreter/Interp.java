package jpp.interpreter;

import java.util.*;

import compiler.parser.ParseTree;

public class Interp {
    private final StringBuilder output = new StringBuilder();
    private final Scanner scan;

    public Interp(){
        scan = new Scanner(System.in);
    }

    public void run(ParseTree pTree){
        switch (pTree.getDescription().name) {

        }
    }

    public String result(){
        return output.toString();
    }
}