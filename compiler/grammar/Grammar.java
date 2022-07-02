package compiler.grammar;
import java.util.*;

import compiler.Printable;
import compiler.Rule;
import compiler.SymbolString;

public class Grammar implements Printable{
    private final List<Rule> rules;
    private final Rule startRule;
    
    private final Map<String, List<Rule>> startsWith;
    private final Set<String> allSymbols, terminals, nonTerminals, nullableSet;
    private final Map<String, Set<String>> firstSets, followSets;
    
    public Grammar(List<Rule> rules, String startSymbol){
        
        this.rules = new ArrayList<>(rules);
        
        // Augment the grammar (if needed)

        startRule = new Rule("__START__", startSymbol);
        this.rules.add(startRule);
        String startSymbol1 = "__START__";
        
        // Classify symbols as terminals or nonterminals
        
        allSymbols = new HashSet<>();
        allSymbols.add("__END__");
        nonTerminals = new HashSet<>();
        startsWith = new HashMap<>();
        
        allSymbols.add(startSymbol1);
        nonTerminals.add(startSymbol1);
        
        for(Rule rule : this.rules){
            nonTerminals.add(rule.getLhs());
            allSymbols.addAll(rule.getRhs().getList());
        }
        
        terminals = new HashSet<>(allSymbols);
        terminals.removeAll(nonTerminals);
        
        // Compute for each symbol the set of rules whose left-hand-sides match
        // said symbol
        
        for(String sym : allSymbols)
            startsWith.put(sym, new ArrayList<>());
        for(Rule rule : this.rules)
            try {
                startsWith.get(rule.getLhs()).add(rule);
            }
            catch (NullPointerException e){
                System.out.println("NullPtrExeption on adding rule to startsWith:\n\t" + rule);
                throw e;
            }
        
        
        //Initialize FIRST sets, FOLLOW sets, nullable set
        
        nullableSet = new HashSet<>();
        firstSets = new HashMap<>();
        followSets = new HashMap<>();
        
        for(String sym : allSymbols){
			firstSets.put(sym, new HashSet<>());
			followSets.put(sym, new HashSet<>());
			if(isTerminal(sym)){
                firstSets.get(sym).add(sym);
            }
		}
        
        followSets.get(startSymbol1).add("__END__");
        
        // Calculate FIRST sets, FOLLOW sets, and the set of nullable symbols
        
        boolean updated = true;
		while(updated) {
		    updated = false;
			for(Rule rule : this.rules){
			    String lhs = rule.getLhs();
			    SymbolString rhs = rule.getRhs();
				boolean brk = false;
				for(String sym : rhs){
					updated |= firstSets.get(lhs).addAll(first(sym));
					if(!isNullable(sym)){
					    brk = true;
					    break;
					}
				}
				if(!brk) updated |= nullableSet.add(lhs);
				
                Set<String> aux = follow(lhs);
				for(int i = rhs.size() - 1; i >= 0; i--){
					if(isNonTerminal(rhs.get(i)))
						updated |= follow(rhs.get(i)).addAll(aux);
					if(isNullable(rhs.get(i))){
						aux = new HashSet<>(aux);
						aux.addAll(first(rhs.get(i)));
					}
					else aux = first(rhs.get(i));
				}
			}
		}
    }
    
    public List<Rule> getRules(){return rules;}
    public List<Rule> getRules(String sym){return startsWith.get(sym);}
    
    public Set<String> getAllSymbols(){return allSymbols;}
    public Set<String> getNonTerminals(){return nonTerminals;}
    public Set<String> getTerminals(){return terminals;}

    public boolean isTerminal(String sym){return terminals.contains(sym);}
    public boolean isNonTerminal(String sym){return nonTerminals.contains(sym);}
    
    public Rule getStartRule(){return startRule;}
    
    // public Strin

    public String toString(){
        return rules.stream().map(Rule::toString).reduce("",(String a, String b)->a+"\n"+b);
    }
    
    public boolean isNullable(SymbolString tkns){
        if(tkns.size() == 0) return true;
        for(String tkn : tkns)
            if(isNullable(tkn))
                return true;
        return false;
    }
    
    public boolean isNullable(String tkn){
        return nullableSet.contains(tkn);
    }
    
    public Set<String> follow(SymbolString tkns){
        // Follow set of empty token string is {epsilon}
        if(tkns.size() == 0)
            return new HashSet<>(Collections.singletonList(null));
        // Otherwise follow set of token string is follow set of last token
        Set<String> res = new HashSet<>(follow(tkns.lastTkn()));
        // If last token is nullable, then also add the follow set of the rest
        // of the token string
        if(isNullable(tkns.lastTkn()))
            res.addAll(follow(tkns.substr(0, tkns.size() - 1)));
        return res;
    }
    public Set<String> follow(String tkn){return followSets.get(tkn);}
    
    public Set<String> first(SymbolString tkns){
        // First set of empty token string is {epsilon}
        if(tkns.size() == 0)
            return new HashSet<>(Collections.singletonList(null));
        // Otherwise first set of token string is first set of the first token
        // of the token string
        Set<String> res = new HashSet<>(first(tkns.firstTkn()));
        // If the first token of the token string is nullable, then also add the
        // first set of the rest of the token string
        if(isNullable(tkns.firstTkn())) res.addAll(first(tkns.substr(1)));
        return res;
    }
    public Set<String> first(String tkn){return firstSets.get(tkn);}
}