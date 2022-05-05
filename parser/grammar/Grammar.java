package parser.grammar;
import java.util.*;

import parser.Printable;
import parser.Rule;
import parser.TokenString;
import parser.ComparableSet;

public class Grammar implements Printable{
    /*private*/ public final List<Rule> rules;
    /*private*/ public final String startSymbol;
    public final Rule startRule;
    
    /*private*/ public final TreeMap<String, List<Rule>> startsWith;
    /*private*/ public final ComparableSet<String> allSymbols, terminals, nonTerminals, nullableSet;
    /*private*/ public final TreeMap<String, ComparableSet<String>> firstSets, followSets;
    
    public Grammar(List<Rule> rules, String startSymbol){
        
        this.rules = new ArrayList<>(rules);
        
        // Augment the grammar (if needed)

        startRule = new Rule("__START__", startSymbol);
        this.rules.add(startRule);
        this.startSymbol = "__START__";
        
        // Classify symbols as terminals or nonterminals
        
        allSymbols = new ComparableSet<>();
        nonTerminals = new ComparableSet<>();
        startsWith = new TreeMap<>();
        
        allSymbols.add(this.startSymbol);
        nonTerminals.add(this.startSymbol);
        
        for(Rule rule : this.rules){
            nonTerminals.add(rule.getLhs());
            allSymbols.addAll(rule.getRhs().getList());
        }
        
        terminals = new ComparableSet<>(allSymbols);
        terminals.removeAll(nonTerminals);
        terminals.add("__END__");
        
        // Compute for each symbol the set of rules whose left-hand-sides match
        // said symbol
        
        for(String sym : allSymbols)
            startsWith.put(sym, new ArrayList<>());
        for(Rule rule : this.rules)
            startsWith.get(rule.getLhs()).add(rule);
        
        
        //Initialize FIRST sets, FOLLOW sets, nullable set
        
        nullableSet = new ComparableSet<>();
        firstSets = new TreeMap<>();
        followSets = new TreeMap<>();
        
        for(String sym : allSymbols){
			firstSets.put(sym, new ComparableSet<>());
			followSets.put(sym, new ComparableSet<>());
			if(isTerminal(sym)) firstSets.get(sym).add(sym);
		}
        
        followSets.get(this.startSymbol).add("__END__");
        
        // Calculate FIRST sets, FOLLOW sets, and the set of nullable symbols
        
        boolean updated = true;
		while(updated) {
		    updated = false;
			for(Rule rule : this.rules){
			    String lhs = rule.getLhs();
			    TokenString rhs = rule.getRhs();
				boolean brk = false;
				for(String sym : rhs){
					updated |= firstSets.get(lhs).addAll(first(sym));
					if(!isNullable(sym)){
					    brk = true;
					    break;
					}
				}
				if(!brk) updated |= nullableSet.add(lhs);
				
                ComparableSet<String> aux = follow(lhs);
				for(int i = rhs.size() - 1; i >= 0; i--){
					if(isNonTerminal(rhs.get(i)))
						updated |= follow(rhs.get(i)).addAll(aux);
					if(isNullable(rhs.get(i))){
						aux = new ComparableSet<>(aux);
						aux.addAll(first(rhs.get(i)));
					}
					else aux = first(rhs.get(i));
				}
			}
		}
		
		// Ok now this grammar ready to go
    }
    
    public List<Rule> getRules(){return rules;}
    public List<Rule> getRules(String sym){return startsWith.get(sym);}
    
    // public Strin

    public String toString(){
        return rules.stream().map(i->i.toString()).reduce("",(String a, String b)->a+"\n"+b);
    }
    
    public boolean isTerminal(String sym){return terminals.contains(sym);}
    public boolean isNonTerminal(String sym){return nonTerminals.contains(sym);}
    
    public boolean isNullable(TokenString tkns){
        if(tkns.size() == 0) return true;
        if(isNullable(tkns.firstTkn())) return isNullable(tkns.substr(1));
        return false;
    }
    
    public boolean isNullable(String tkn){
        return nullableSet.contains(tkn);
    }
    
    public ComparableSet<String> follow(TokenString tkns){
        // Follow set of empty token string is {epsilon}
        if(tkns.size() == 0)
            return new ComparableSet<>(Arrays.asList(new String[]{null}));
        // Otherwise follow set of token string is follow set of last token
        ComparableSet<String> res = new ComparableSet<>(follow(tkns.lastTkn()));
        // If last token is nullable, then also add the follow set of the rest
        // of the token string
        if(isNullable(tkns.lastTkn()))
            res.addAll(follow(tkns.substr(0, tkns.size() - 1)));
        return res;
    }
    public ComparableSet<String> follow(String tkn){return followSets.get(tkn);}
    
    public ComparableSet<String> first(TokenString tkns){
        // First set of empty token string is {epsilon}
        if(tkns.size() == 0)
            return new ComparableSet<>(Arrays.asList(new String[]{"__EPSILON__"}));
        // Otherwise first set of token string is first set of the first token
        // of the token string
        ComparableSet<String> res = new ComparableSet<>(first(tkns.firstTkn()));
        // If the first token of the token string is nullable, then also add the
        // first set of the rest of the token string
        if(isNullable(tkns.firstTkn())) res.addAll(first(tkns.substr(1)));
        return res;
    }
    public ComparableSet<String> first(String tkn){return firstSets.get(tkn);}
}