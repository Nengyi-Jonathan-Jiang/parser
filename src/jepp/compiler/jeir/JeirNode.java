package jepp.compiler.jeir;

import jepp.jevm.Instruction;

public interface JeirNode {

    class JeirStatements implements JeirNode {

    }

    class JeirPrintStatement implements JeirNode {

    }

    abstract class JeirArithmeticStatement implements JeirNode {
        public final Instruction.LogicalInstruction instruction;

        protected JeirArithmeticStatement(Instruction.LogicalInstruction instruction) {
            this.instruction = instruction;
        }
    }
}
