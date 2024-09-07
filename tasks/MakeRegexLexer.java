import frontend.Symbol;
import frontend.lexer.regexEngine.DFA;
import frontend.lexer.regexEngine.DFA2NFA;
import frontend.lexer.regexEngine.NFA;
import frontend.lexer.regexEngine.RegexNode;

import java.util.Map;

public class MakeRegexLexer {
    public static void main(String[] args) {
        RegexNode comment_regex = new RegexNode.AlternationNode(
            new RegexNode.ConcatenationNode(
                new RegexNode.LeafNode('/'),
                new RegexNode.LeafNode('/'),
                new RegexNode.KleeneStarNode(
                    new RegexNode.LeafNode('/', '*', 'a')
                )
            ),
            new RegexNode.ConcatenationNode(
                new RegexNode.LeafNode('/'),
                new RegexNode.LeafNode('*'),

                new RegexNode.KleeneStarNode(
                    new RegexNode.AlternationNode(
                        new RegexNode.LeafNode('/', 'a', 'n'),
                        new RegexNode.ConcatenationNode(
                            new RegexNode.KleeneStarNode(
                                new RegexNode.LeafNode('*')
                            ),
                            new RegexNode.LeafNode('a', 'n')
                        )
                    )
                ),

                new RegexNode.KleeneStarNode(
                    new RegexNode.LeafNode('*')
                ),

                new RegexNode.LeafNode('*'),
                new RegexNode.LeafNode('/')
            )
        );

        NFA comment_nfa = comment_regex.createNFA();

        comment_nfa.print();

        System.out.println("creating dfa...");

        DFA dfa = DFA2NFA.to_dfa(Map.of(new Symbol.SymbolTable().create("comment"), comment_nfa));

        dfa.print();

        System.out.println("done.");
    }
}
