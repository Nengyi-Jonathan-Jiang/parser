package compiler.jevm;

import java.util.function.BinaryOperator;
import java.util.function.Function;

import static compiler.jevm.Instruction.Param.ParamType.*;
import static compiler.jevm.MemoryLocation.*;

public abstract class Instruction {
    private static class ArithmeticInstruction extends Instruction {
        private final Param p1, p2, dest;
        private final Param.ParamType t1, t2, td;
        private final BinaryOperator<Integer> intFunc;
        private final BinaryOperator<Float> floatFunc;

        public ArithmeticInstruction(Param p1, Param p2, Param dest, BinaryOperator<Integer> intFunc, BinaryOperator<Float> floatFunc) {
            t1 = (this.p1 = p1).type;
            t2 = (this.p2 = p2).type;
            td = (this.dest = dest).type;
            this.intFunc = intFunc;
            this.floatFunc = floatFunc;
        }

        @Override
        public void execute(VM jevm) {
            if (!t1.isIntOrFloat() || !t2.isIntOrFloat() || !td.isIntOrFloat())
                throw new Error("JeVM Error: Invalid parameters to arithmetic operation : (" + t1 + ", " + t2 + ") -> " + td);

            var x1 = (M4) p1.getMem(jevm);
            var x2 = (M4) p2.getMem(jevm);
            var d = (M4) dest.getMem(jevm);

            if (t1.isInt() && t2.isInt() && td.isInt())
                d.setInt(intFunc.apply(x1.getInt(), x2.getInt()));
            else if (t1.isInt() && t2.isFloat() && td.isFloat())
                d.setFloat(floatFunc.apply((float) x1.getInt(), x2.getFloat()));
            else if (t1.isFloat() && t2.isInt() && td.isFloat())
                d.setFloat(floatFunc.apply(x1.getFloat(), (float) x2.getInt()));
            else if (t1.isFloat() && t2.isFloat() && td.isFloat())
                d.setFloat(floatFunc.apply(x1.getFloat(), x2.getFloat()));
            else
                throw new Error("JeVM Error: Invalid parameters to arithmetic operation : (" + t1 + ", " + t2 + ") -> " + td);
        }
    }

    private static class LogicalInstruction extends Instruction {
        private final Param p1, p2, dest;
        private final Param.ParamType t1, t2, td;
        private final BinaryOperator<Integer> intFunc;
        private final BinaryOperator<Boolean> boolFunc;

        public LogicalInstruction(Param p1, Param p2, Param dest, BinaryOperator<Integer> intFunc, BinaryOperator<Boolean> boolFunc) {
            t1 = (this.p1 = p1).type;
            t2 = (this.p2 = p2).type;
            td = (this.dest = dest).type;
            this.intFunc = intFunc;
            this.boolFunc = boolFunc;
        }

        @Override
        public void execute(VM jevm) {
            var x1 = p1.getMem(jevm);
            var x2 = p2.getMem(jevm);
            var d = dest.getMem(jevm);

            if (t1.isInt() && t2.isInt() && td.isInt())
                ((M4) d).setInt(intFunc.apply(
                        ((M4) x1).getInt(),
                        ((M4) x2).getInt()
                ));
            else if (t1.isBool() && t2.isBool() && td.isBool())
                ((M1) d).setBool(boolFunc.apply(
                        ((M1) x1).getBool(),
                        ((M1) x2).getBool()
                ));
            else
                throw new Error("JeVM Error: Invalid parameters to logical operation : (" + t1 + ", " + t2 + ") -> " + td);
        }
    }

    private static class ShiftInstruction extends Instruction {
        private final Param p1, p2, dest;
        private final Param.ParamType t1, t2, td;
        private final BinaryOperator<Integer> func;

        public ShiftInstruction(Param p1, Param p2, Param dest, BinaryOperator<Integer> func) {
            t1 = (this.p1 = p1).type;
            t2 = (this.p2 = p2).type;
            td = (this.dest = dest).type;
            this.func = func;
        }

        @Override
        public void execute(VM jevm) {
            if (!t1.isInt() || !t2.isInt() || !td.isInt())
                throw new Error("JeVM Error: Invalid parameters to bit shift operation : (" + t1 + ", " + t2 + ") -> " + td);

            var x1 = (M4) p1.getMem(jevm);
            var x2 = (M4) p2.getMem(jevm);
            var d = (M4) dest.getMem(jevm);

            d.setInt(func.apply(x1.getInt(), x2.getInt()));
        }
    }

    public static final Instruction NOOP = new Instruction() { public void execute(VM jevm) {} };

    public static class MOV extends Instruction {
        private final Param source, dest;

        public MOV(Param source, Param dest) {
            this.source = source;
            this.dest = dest;
        }

        @Override
        public void execute(VM jevm) {
            dest.getMem(jevm).copyFrom(source.getMem(jevm));
        }
    }

    public static class ADD extends ArithmeticInstruction {
        public ADD(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, Integer::sum, Float::sum);
        }
    }

    public static class SUB extends ArithmeticInstruction {
        public SUB(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a - b, (a, b) -> a - b);
        }
    }

    public static class MUL extends ArithmeticInstruction {
        public MUL(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a * b, (a, b) -> a * b);
        }
    }

    public static class DIV extends ArithmeticInstruction {
        public DIV(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a / b, (a, b) -> a / b);
        }
    }

    public static class MOD extends ArithmeticInstruction {
        public MOD(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> (a % b + b) % b, (a, b) -> (a % b + b) % b);
        }
    }

    public static class OR extends LogicalInstruction {
        public OR(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a | b, (a, b) -> a || b);
        }
    }

    public static class AND extends LogicalInstruction {
        public AND(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a & b, (a, b) -> a && b);
        }
    }

    public static class XOR extends LogicalInstruction {
        public XOR(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a ^ b, (a, b) -> a != b);
        }
    }

    public static class SHL extends ShiftInstruction {
        public SHL(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a << b);
        }
    }

    public static class SHR extends ShiftInstruction {
        public SHR(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a >> b);
        }
    }

    public static class JMP extends Instruction {
        private final Param p, dest;
        private final byte condition;

        public static final byte GZ = 1, LZ = 2, EZ = 4;

        public JMP(Param p, Param dest, byte condition) {
            this.p = p;
            this.dest = dest;
            this.condition = condition;
        }

        @Override
        public void execute(VM jevm) {
            var x = p.getMem(jevm);
            var t = p.type;
            if (dest.type != INT) throw new Error("JeVM Error: Jump destination must be an integer");

            int comp = switch (t) {
                case FLOAT -> Float.compare(((M4) x).getFloat(), 0);
                case INT -> ((M4) x).getInt();
                case CHAR -> ((M1) x).getChar() & 0xFF;
                case BOOL -> ((M1) x).getBool() ? 1 : 0;
            };

            if ((condition & GZ) != 0 && comp > 0 || (condition & LZ) != 0 && comp < 0 || (condition & EZ) != 0 && comp == 0) {
                jevm.jump(((M4) dest.getMem(jevm)).getInt());
            }
        }
    }

    public static class CPY extends Instruction {
        private final Param source, dest, length;

        public CPY(Param source, Param dest, Param length) {
            this.source = source;
            this.dest = dest;
            this.length = length;
        }

        @Override
        public void execute(VM jevm) {
            if (!source.type.isInt() || !dest.type.isInt() || !length.type.isInt()) {
                throw new Error("JeVM Error: Parameters to CPY must be integers");
            }
            var s = (M4) source.getMem(jevm);
            var d = (M4) dest.getMem(jevm);
            var l = (M4) length.getMem(jevm);

            jevm.ram.memcpy(s.getInt(), d.getInt(), l.getInt());
        }
    }

    public static class DSP extends Instruction {
        private final Param source;
        private final Param.ParamType t;

        public DSP(Param source) {
            t = (this.source = source).type;
        }

        @Override
        public void execute(VM jevm) {
            MemoryLocation s = source.getMem(jevm);
            jevm.display(switch (t) {
                case FLOAT -> String.valueOf(((M4) s).getFloat());
                case INT -> String.valueOf(((M4) s).getInt());
                case CHAR -> String.valueOf(((M1) s).getChar());
                case BOOL -> String.valueOf(((M1) s).getBool());
            });
        }
    }

    public static class NEW extends Instruction {
        private final Param source, dest;

        public NEW(Param source, Param dest) {
            this.source = source;
            this.dest = dest;
        }

        @Override
        public void execute(VM jevm) {
            if(!source.type().isInt() || !dest.type.isInt()) throw new Error("JeVM Error: Invalid parameter types to alloc");
            M4 s = (M4) source.getMem(jevm), d = (M4) dest.getMem(jevm);
            d.setInt(jevm.allocator.allocate(s.getInt()));
        }
    }
    public static class DEL extends Instruction {
        private final Param source;
        public DEL(Param source) {
            this.source = source;
        }

        @Override
        public void execute(VM jevm) {
            if(!source.type().isInt()) throw new Error("JeVM Error: Invalid parameter types to dealloc");
            M4 s = (M4) source.getMem(jevm);
            jevm.allocator.deallocate(s.getInt());
        }
    }

    public static class CMP extends Instruction {
        private final Param p, dest;
        private final byte condition;

        public static final byte GZ = 1, LZ = 2, EZ = 4;

        public CMP(Param p, Param dest, byte condition) {
            this.p = p;
            this.dest = dest;
            this.condition = condition;
        }

        @Override
        public void execute(VM jevm) {
            var x = p.getMem(jevm);
            var t = p.type;
            if (dest.type != BOOL) throw new Error("JeVM Error: Jump destination must be an integer");

            int comp = switch (t) {
                case FLOAT -> Float.compare(((M4) x).getFloat(), 0);
                case INT -> ((M4) x).getInt();
                case CHAR -> ((M1) x).getChar() & 0xFF;
                case BOOL -> ((M1) x).getBool() ? 1 : 0;
            };

            boolean res = false;
            if((condition & GZ) != 0) res |= comp > 0;
            if((condition & LZ) != 0) res |= comp < 0;
            if((condition & EZ) != 0) res |= comp == 0;

            ((M1) dest.getMem(jevm)).setBool(res);
        }
    }

    public abstract void execute(VM jevm);

    public record Param(Function<VM, MemoryLocation> location, ParamType type) {
        public static Param constant(int constVal) {
            return new Param($ -> new Constant4(constVal), INT);
        }

        public static Param constant(float constVal) {
            return new Param($ -> new Constant4(constVal), FLOAT);
        }

        public static Param constant(char constVal) {
            return new Param($ -> new Constant1(constVal), CHAR);
        }

        public static Param constant(boolean constVal) {
            return new Param($ -> new Constant1(constVal), BOOL);
        }

        public static Param register1(int registerId, ParamType type) {
            if (!type.isBoolOrChar())
                throw new Error("JeVM Error: Invalid type for 1 byte register");
            return new Param(vm -> vm.getR1(registerId, false), type);
        }

        public static Param register4(int registerId, ParamType type) {
            if (!type.isIntOrFloat())
                throw new Error("JeVM Error: Invalid type for 4 byte register");
            return new Param(vm -> vm.getR4(registerId, false), type);
        }

        public static Param mem1(int registerId, ParamType type) {
            if (!type.isBoolOrChar())
                throw new Error("JeVM Error: Invalid type for 1 byte memory");
            return new Param(vm -> vm.getR1(registerId, true), type);
        }

        public static Param mem4(int registerId, ParamType type) {
            if (!type.isIntOrFloat())
                throw new Error("JeVM Error: Invalid type for 4 byte memory");
            return new Param(vm -> vm.getR4(registerId, true), type);
        }

        public enum ParamType {
            FLOAT, INT, CHAR, BOOL;

            public boolean isInt() {
                return this == INT;
            }

            public boolean isFloat() {
                return this == FLOAT;
            }

            public boolean isBool() {
                return this == BOOL;
            }

            public boolean isChar() {
                return this == CHAR;
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            public boolean isIntOrFloat() {
                return isInt() || isFloat();
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            public boolean isBoolOrChar() {
                return isBool() || isChar();
            }
        }

        public MemoryLocation getMem(VM vm) {
            return location().apply(vm);
        }
    }
}