package frontend.lexer.fsm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface RegexNode {
    NFA createNFA();

    // A Regex node that is not a leaf node
    abstract sealed class ParentNode implements RegexNode permits AlternationNode, ConcatenationNode, KleeneStarNode, OptionalNode {
        public final RegexNode[] children;

        public ParentNode(RegexNode... children) {
            this.children = children;
        }

        protected NFA[] getChildrenNFAs() {
            NFA[] result = new NFA[children.length];
            for (int i = 0; i < children.length; i++) {
                result[i] = children[i].createNFA();
            }
            return result;
        }
    }

    final class AlternationNode extends ParentNode {
        public AlternationNode(RegexNode... children) {
            super(children);
        }

        @Override
        public NFA createNFA() {
            // To create an alternation, we
            // 1) add epsilon transitions from INITIAL_STATE to each remapped initialState and
            // 2) redirect all transitions to each remapped acceptingState to ACCEPTING_STATE

            NFA result = new NFA();

            for (NFA childNFA : getChildrenNFAs()) {
                int newState = NFA.getNextUnusedState();
                childNFA.remap(newState, NFA.ACCEPTING_STATE);
                result.mergeWith(childNFA);
                result.addTransition(NFA.INITIAL_STATE, NFA.EPSILON, newState);
            }

            return result;
        }
    }
    final class KleeneStarNode extends ParentNode {
        public KleeneStarNode(RegexNode child) {
            super(child);
        }

        @Override
        public NFA createNFA() {
            NFA childNFA = children[0].createNFA();
            int newState = NFA.getNextUnusedState();
            childNFA.remap(NFA.INITIAL_STATE, newState);
            childNFA.addTransition(NFA.INITIAL_STATE, NFA.EPSILON, newState);
            childNFA.addTransition(newState, NFA.EPSILON, NFA.INITIAL_STATE);
            childNFA.addTransition(newState, NFA.EPSILON, NFA.ACCEPTING_STATE);
            return childNFA;
        }
    }

    final class OptionalNode extends ParentNode {
        OptionalNode(RegexNode child) {
            super(child);
        }

        @Override
        public NFA createNFA() {
            NFA childNFA = children[0].createNFA();
            childNFA.addTransition(NFA.INITIAL_STATE, NFA.EPSILON, NFA.ACCEPTING_STATE);
            return childNFA;
        }
    }

    final class ConcatenationNode extends ParentNode {

        public ConcatenationNode(RegexNode... children) {
            super(children);
        }

        @Override
        public NFA createNFA() {
            NFA result = new NFA();

            boolean isFirstChild = true;

            for(NFA childNFA : getChildrenNFAs()) {
                if(isFirstChild) {
                    // For the first child, simply copy it into result
                    result = childNFA;
                    isFirstChild = false;
                    continue;
                }

                // For each other child, we need to merge the current
                // end state with the new child's start state
                int newState = NFA.getNextUnusedState();
                result.remap(NFA.INITIAL_STATE, newState);
                childNFA.remap(newState, NFA.ACCEPTING_STATE);
                result.mergeWith(childNFA);
            }

            return result;
        }
    }

    // This is pretty self-explanatory
    final class LeafNode implements RegexNode {
        private final Set<Character> characters;

        public LeafNode(char... characters) {
            // Bruh java... why...
            this(new String(characters).chars().mapToObj(i -> (char)i).collect(Collectors.toUnmodifiableSet()));
        }

        public LeafNode(Set<Character> characters) {
            this.characters = characters;
        }

        @Override
        public NFA createNFA() {
            NFA result = new NFA();
            for (char c : characters) {
                result.addTransition(NFA.INITIAL_STATE, c, NFA.ACCEPTING_STATE);
            }
            return result;
        }
    }
}