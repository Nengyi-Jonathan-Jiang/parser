package compiler.grammar;
import java.util.*;
import java.util.stream.Collectors;

import compiler.*;
import compiler.util.*;

public class Grammar implements Printable{
    private final List<Rule> rules;
    private final Rule startRule;
    
    private final Map<Symbol, List<Rule>> startsWith;
    private final SymbolSet allSymbols, terminals, nonTerminals, nullableComparableSet;
    private final Map<Symbol, SymbolSet> firstComparableSets, followComparableSets;
    private final Cache<SymbolString, SymbolSet> firstCache = new HashCompareCache<>(), followCache = new HashCompareCache<>();

    public final Symbol.SymbolTable symbolTable;

    public Grammar(List<Rule> rules, Symbol startSymbol, Symbol.SymbolTable symbolTable){
        
        this.rules = new ArrayList<>(rules);

        // Augment the grammar
        startRule = new Rule(symbolTable.__START__, startSymbol);
        this.symbolTable = symbolTable;
        this.rules.add(startRule);
        
        // Classify symbols as terminals or nonterminals
        
        allSymbols = new SymbolSet();
        allSymbols.add(symbolTable.__END__);
        nonTerminals = new SymbolSet();
        startsWith = new HashMap<>();
        
        allSymbols.add(symbolTable.__START__);
        nonTerminals.add(symbolTable.__START__);
        
        for(Rule rule : this.rules){
            nonTerminals.add(rule.getLhs());
            allSymbols.addAll(rule.getRhs().getList());
        }
        
        terminals = new SymbolSet(allSymbols);
        terminals.removeAll(nonTerminals);
        
        // Compute for each symbol the set of rules whose left-hand-sides match
        // said symbol
        
        for(Symbol sym : allSymbols)
            startsWith.put(sym, new ArrayList<>());
        for(Rule rule : this.rules) {
            try {
                startsWith.get(rule.getLhs()).add(rule);
            }
            catch (NullPointerException e){
                System.out.println("NullPtrException on adding rule to startsWith:\n\t" + rule);
                throw e;
            }
        }
        
        
        //Initialize FIRST sets, FOLLOW sets, nullable set
        
        nullableComparableSet = new SymbolSet();
        firstComparableSets = new HashMap<>();
        followComparableSets = new HashMap<>();
        
        for(Symbol sym : allSymbols){
			firstComparableSets.put(sym, new SymbolSet());
			followComparableSets.put(sym, new SymbolSet());
			if(isTerminal(sym)){
                firstComparableSets.get(sym).add(sym);
            }
		}
        
        followComparableSets.get(symbolTable.__START__).add(symbolTable.__END__);
        
        // Calculate FIRST sets, FOLLOW sets, and the set of nullable symbols
        
        boolean updated = true;
		while(updated) {
		    updated = false;
			for(Rule rule : this.rules){
			    Symbol lhs = rule.getLhs();
			    SymbolString rhs = rule.getRhs();
				boolean brk = false;
				for(Symbol sym : rhs){
					updated |= firstComparableSets.get(lhs).addAll(first(sym));
					if(!isNullable(sym)){
					    brk = true;
					    break;
					}
				}
				if(!brk) updated |= nullableComparableSet.add(lhs);
				
                ComparableSet<Symbol> aux = follow(lhs);
				for(int i = rhs.size() - 1; i >= 0; i--){
					if(isNonTerminal(rhs.get(i)))
						updated |= follow(rhs.get(i)).addAll(aux);
					if(isNullable(rhs.get(i))){
						aux = new ComparableHashSet<>(aux);
						aux.addAll(first(rhs.get(i)));
					}
					else aux = first(rhs.get(i));
				}
			}
		}
    }
    
    public List<Rule> getRules(){return rules;}
    public List<Rule> getRules(Symbol sym){return startsWith.get(sym);}
    
    public ComparableSet<Symbol> getAllSymbols(){return allSymbols;}
    public ComparableSet<Symbol> getNonTerminals(){return nonTerminals;}
    public ComparableSet<Symbol> getTerminals(){return terminals;}

    public boolean isTerminal(Symbol sym){return terminals.contains(sym);}
    public boolean isNonTerminal(Symbol sym){return nonTerminals.contains(sym);}
    
    public Rule getStartRule(){return startRule;}

    public String toString(){
        return rules.stream().map(Rule::toString).collect(Collectors.joining("\n"));
    }
    
    public boolean isNullable(SymbolString tkns){
        if(tkns.size() == 0) return true;
        for(Symbol tkn : tkns)
            if(isNullable(tkn))
                return true;
        return false;
    }
    
    public boolean isNullable(Symbol tkn){
        return nullableComparableSet.contains(tkn);
    }
    
    public ComparableSet<Symbol> follow(SymbolString tkns){
        // Follow set of empty token string is {epsilon}
        if(tkns.size() == 0) return new ComparableHashSet<>(Collections.singletonList(null));

        // Check result in cache
        {
            var cachedResult = followCache.get(tkns);
            if(cachedResult != null) return cachedResult;
        }
        // Otherwise follow set of token string is follow set of last token
        SymbolSet res = new SymbolSet(follow(tkns.lastTkn()));

        // If last token is nullable, then also add the follow set of the rest
        // of the token string
        if(isNullable(tkns.lastTkn())) res.addAll(follow(tkns.substr(0, tkns.size() - 1)));

        // Cache result
        followCache.cache(tkns, res);

        return res;
    }
    public SymbolSet follow(Symbol tkn){
        return followComparableSets.get(tkn);
    }
    
    public SymbolSet first(SymbolString tkns){
        // Check result in cache
        {
            var cachedResult = firstCache.get(tkns);
            if(cachedResult != null) return cachedResult;
        }

        // First set of empty token string is just { epsilon }
        if(tkns.size() == 0) return new SymbolSet(Collections.singletonList(null));


        // Otherwise first set of token string is first set of the first token
        // of the token string
        SymbolSet res = new SymbolSet(first(tkns.firstTkn()));
        // If the first token of the token string is nullable, then also add the
        // first set of the rest of the token string
        if(isNullable(tkns.firstTkn())) res.addAll(first(tkns.substr(1)));

        // Cache result
        firstCache.cache(tkns, res);

        return res;
    }
    public SymbolSet first(Symbol tkn){
        return firstComparableSets.get(tkn);
    }
}