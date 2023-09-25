package jepp.compiler.jeir;

import jepp.jevm.Instruction;
import jepp.interpreter.language.JeppMethodPrototype;

public sealed interface JeirNode permits JeirNode.JeirArithmeticStatement, JeirNode.JeirHeapAllocateStatement, JeirNode.JeirMoveStatement, JeirNode.JeirPrintStatement, JeirNode.JeirPrintVarStatement, JeirNode.JeirStatements, JeirNode.JeirStaticHeapAllocateStatement, JeirNode.JeirStoreStatement {

    record JeirStatements(JeirNode... children) implements JeirNode {}

    record JeirPrintStatement(String value) implements JeirNode {}

    record JeirPrintVarStatement(String var) implements JeirNode {}

    record JeirMoveStatement(String from, String to) implements JeirNode {}
    record JeirStoreStatement(Object value, String to) implements JeirNode{}

    record JeirHeapAllocateStatement(String size, String to) implements JeirNode {}
    record JeirStaticHeapAllocateStatement(int size, String to) implements JeirNode {}

    record JeirArithmeticStatement(Instruction.BinaryInstruction instruction) implements JeirNode {

    }

    record JeirFunctionDeclaration(JeppMethodPrototype prototype, JeirStatements body) {}
}
