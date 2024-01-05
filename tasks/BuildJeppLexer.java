import frontend.lexer.fsm.NFA;
import frontend.lexer.fsm.RegexNode;

public class BuildJeppLexer {
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

        for(var entry : comment_nfa.table.entrySet()) {
            int state = entry.getKey();
            for(var entry2 : entry.getValue().entrySet()) {
                char c = entry2.getKey();
                System.out.println(state + " --" + c + "-> " + entry2.getValue());
            }
        }

        System.out.println("done.");
    }
}
