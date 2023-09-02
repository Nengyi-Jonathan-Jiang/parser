package jepp.jevm;

import java.util.function.BinaryOperator;

import static jepp.jevm.Instruction.Param.ParamType.*;
import static jepp.jevm.MemoryLocation.*;

public abstract class Instruction {
    private interface BinOp {
        void apply(MemoryLocation x1, MemoryLocation x2, MemoryLocation d);
    }

    private static class ArithmeticInstruction extends Instruction {
        protected final Param p1;
        protected final Param p2;
        protected final Param dest;
        private final BinOp func;

        public ArithmeticInstruction(Param p1, Param p2, Param dest, BinaryOperator<Integer> intFunc, BinaryOperator<Float> floatFunc) {
            Param.ParamType t1 = (this.p1 = p1).type, t2 = (this.p2 = p2).type, td = (this.dest = dest).type;

            if (t1.isInt() && t2.isInt() && td.isInt())
                func = (x1, x2, d) -> d.setInt(intFunc.apply(x1.getInt(), x2.getInt()));
            else if (t1.isInt() && t2.isFloat() && td.isFloat())
                func = (x1, x2, d) -> d.setFloat(floatFunc.apply((float) x1.getInt(), x2.getFloat()));
            else if (t1.isFloat() && t2.isInt() && td.isFloat())
                func = (x1, x2, d) -> d.setFloat(floatFunc.apply(x1.getFloat(), (float) x2.getInt()));
            else if (t1.isFloat() && t2.isFloat() && td.isFloat())
                func = (x1, x2, d) -> d.setFloat(floatFunc.apply(x1.getFloat(), x2.getFloat()));
            else
                throw new Error("JeVM Error: Invalid parameters to arithmetic operation : (" + t1 + ", " + t2 + ") -> " + td);
        }

        @Override
        public void execute(VM jevm) {
            func.apply(p1.getMem(jevm), p2.getMem(jevm), dest.getMem(jevm));
        }
    }

    private static class LogicalInstruction extends Instruction {
        protected final Param p1;
        protected final Param p2;
        protected final Param dest;
        private final Param.ParamType t1, t2, td;
        private final BinaryOperator<Integer> intFunc;

        public LogicalInstruction(Param p1, Param p2, Param dest, BinaryOperator<Integer> intFunc) {
            t1 = (this.p1 = p1).type;
            t2 = (this.p2 = p2).type;
            td = (this.dest = dest).type;
            this.intFunc = intFunc;
        }

        @Override
        public void execute(VM jevm) {
            var x1 = p1.getMem(jevm);
            var x2 = p2.getMem(jevm);
            var d = dest.getMem(jevm);

            if (t1.isInt() && t2.isInt() && td.isInt())
                d.setInt(intFunc.apply(
                        x1.getInt(),
                        x2.getInt()
                ));
            else
                throw new Error("JeVM Error: Invalid parameters to logical operation : (" + t1 + ", " + t2 + ") -> " + td);
        }
    }

    private static class ShiftInstruction extends Instruction {
        protected final Param p1;
        protected final Param p2;
        protected final Param dest;
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

            var x1 =  p1.getMem(jevm);
            var x2 =  p2.getMem(jevm);
            var d =  dest.getMem(jevm);

            d.setInt(func.apply(x1.getInt(), x2.getInt()));
        }
    }

    public static final Instruction NOOP = new Instruction() {
        public void execute(VM jevm) {}

        @Override
        public String toString() {
            return "NOOP";
        }
    };

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

        @Override
        public String toString() {
            return source + " -> " + dest;
        }
    }

    public static class ADD extends ArithmeticInstruction {
        public ADD(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, Integer::sum, Float::sum);
        }

        @Override
        public String toString() {
            return p1 + " + " + p2 + " -> " + dest;
        }
    }

    public static class SUB extends ArithmeticInstruction {
        public SUB(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a - b, (a, b) -> a - b);
        }

        @Override
        public String toString() {
            return p1 + " - " + p2 + " -> " + dest;
        }
    }

    public static class MUL extends ArithmeticInstruction {
        public MUL(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a * b, (a, b) -> a * b);
        }

        @Override
        public String toString() {
            return p1 + " * " + p2 + " -> " + dest;
        }
    }

    public static class DIV extends ArithmeticInstruction {
        public DIV(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a / b, (a, b) -> a / b);
        }

        @Override
        public String toString() {
            return p1 + " / " + p2 + " -> " + dest;
        }
    }

    public static class MOD extends ArithmeticInstruction {
        public MOD(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> (a % b + b) % b, (a, b) -> (a % b + b) % b);
        }

        @Override
        public String toString() {
            return p1 + " mod " + p2 + " -> " + dest;
        }
    }

    public static class OR extends LogicalInstruction {
        public OR(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a | b);
        }

        @Override
        public String toString() {
            return p1 + " | " + p2 + " -> " + dest;
        }
    }

    public static class AND extends LogicalInstruction {
        public AND(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a & b);
        }

        @Override
        public String toString() {
            return p1 + " & " + p2 + " -> " + dest;
        }
    }

    public static class XOR extends LogicalInstruction {
        public XOR(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a ^ b);
        }

        @Override
        public String toString() {
            return p1 + " ^ " + p2 + " -> " + dest;
        }
    }

    public static class SHL extends ShiftInstruction {
        public SHL(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a << b);
        }

        @Override
        public String toString() {
            return p1 + " << " + p2 + " -> " + dest;
        }
    }

    public static class SHR extends ShiftInstruction {
        public SHR(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a >> b);
        }

        @Override
        public String toString() {
            return p1 + " >> " + p2 + " -> " + dest;
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
                case FLOAT -> Float.compare(x.getFloat(), 0);
                case INT -> x.getInt();
            };

            if ((condition & GZ) != 0 && comp > 0 || (condition & LZ) != 0 && comp < 0 || (condition & EZ) != 0 && comp == 0) {
                jevm.jump(dest.getMem(jevm).getInt());
            }
        }

        @Override
        public String toString() {
            return "jump to " + dest + " if " + p + " " + switch (condition) {
                case GZ -> ">";
                case LZ -> "<";
                case GZ | LZ -> "!=";
                case EZ -> "==";
                case GZ | EZ -> ">=";
                case LZ | EZ -> "<=";
                case GZ | LZ | EZ -> "<=>";
                default -> "??";
            } + " 0";
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
            var s =  source.getMem(jevm);
            var d =  dest.getMem(jevm);
            var l =  length.getMem(jevm);

            jevm.ram.memcpy(s.getInt(), d.getInt(), l.getInt());
        }

        @Override
        public String toString() {
            return "copy " + length + " bytes " + source + " -> " + dest;
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
                case FLOAT -> String.valueOf(s.getFloat());
                case INT -> String.valueOf(s.getInt());
            });
        }

        @Override
        public String toString() {
            return source + " -> stdout";
        }
    }

    public static class PRINT extends Instruction {
        private final String s;

        public PRINT(String s) {
            this.s = s;
        }

        @Override
        public void execute(VM jevm) {
            jevm.display(s);
        }
    }

    public static class INP extends Instruction {
        private final Param dest;

        public INP(Param dest) {
            this.dest = dest;
        }

        @Override
        public void execute(VM jevm) {
            var m = dest.getMem(jevm);

            switch (dest.type) {
                case INT -> m.setInt(jevm.readInt());
                case FLOAT -> m.setFloat(jevm.readFloat());
            }
        }

        @Override
        public String toString() {
            return "stdin -> " + dest;
        }
    }

    public static class NEW extends Instruction {
        private final Param size, dest;

        public NEW(Param source, Param dest) {
            this.size = source;
            this.dest = dest;
        }

        @Override
        public void execute(VM jevm) {
            if(!size.type().isInt() || !dest.type.isInt()) throw new Error("JeVM Error: Invalid parameter types to alloc");
            MemoryLocation s = size.getMem(jevm), d = dest.getMem(jevm);
            d.setInt(jevm.allocator.allocate(s.getInt()));
        }

        @Override
        public String toString() {
            return dest + " <- new " + size + " bytes";
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
            var s = source.getMem(jevm);
            jevm.allocator.deallocate(s.getInt());
        }

        @Override
        public String toString() {
            return "del " + source;
        }
    }

    public static class CMP extends Instruction {
        private final Param source, dest;
        private final byte condition;

        public static final byte GZ = 1, LZ = 2, EZ = 4;

        public CMP(Param p, Param dest, byte condition) {
            this.source = p;
            this.dest = dest;
            this.condition = condition;
        }

        @Override
        public void execute(VM jevm) {
            var x = source.getMem(jevm);
            var t = source.type;
            if (dest.type != INT) throw new Error("JeVM Error: Jump destination must be an integer");

            int comp = switch (t) {
                case FLOAT -> Float.compare(x.getFloat(), 0);
                case INT -> x.getInt();
            };

            boolean res = false;
            if((condition & GZ) != 0) res |= comp > 0;
            if((condition & LZ) != 0) res |= comp < 0;
            if((condition & EZ) != 0) res |= comp == 0;

            dest.getMem(jevm).setInt(res ? 1 : 0);
        }


        @Override
        public String toString() {
            return dest + " <- " + source + " " + switch (condition) {
                case GZ -> ">";
                case LZ -> "<";
                case GZ | LZ -> "!=";
                case EZ -> "==";
                case GZ | EZ -> ">=";
                case LZ | EZ -> "<=";
                case GZ | LZ | EZ -> "<=>";
                default -> " ?? ";
            } + " 0";
        }
    }

    public abstract void execute(VM jevm);

    public record Param(LocationSupplier location, ParamType type, String inputName) {
        private interface LocationSupplier {
            MemoryLocation in(VM vm);
        }
        private static LocationSupplier constantSupplier(Constant c){
            return $ -> c;
        }

        public static Param constant(int constVal) {
            return new Param(constantSupplier(new Constant(constVal)), INT, String.valueOf(constVal));
        }

        public static Param constant(float constVal) {
            return new Param(constantSupplier(new Constant(constVal)), FLOAT, String.valueOf(constVal));
        }

        public static Param reg(int registerId, ParamType type) {
            return new Param(vm -> vm.getRegister(registerId), type, "r" + registerId);
        }

        public static Param mem(int registerId, ParamType type) {
            return new Param(vm -> vm.getMemory(registerId), type, "@r" + registerId);
        }

        public enum ParamType {
            FLOAT, INT;

            public boolean isInt() {
                return this == INT;
            }

            public boolean isFloat() {
                return this == FLOAT;
            }
        }

        public MemoryLocation getMem(VM vm) {
            return location.in(vm);
        }

        @Override
        public String toString() {
            return "(" + type.toString().toLowerCase() + " " + inputName.replaceAll("r-1", "sp") + ")";
        }
    }
}