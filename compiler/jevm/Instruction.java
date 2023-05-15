package compiler.jevm;

import java.util.function.BinaryOperator;
import java.util.function.Function;

public abstract class Instruction {
    public static final Instruction NOOP = new Instruction() {public void execute(VM jevm){}};

    public static class MOV extends Instruction {
        private final Param source, dest;

        public MOV(Param source, Param dest){
            this.source = source;
            this.dest = dest;
        }

        @Override
        public void execute(VM jevm) {
            dest.getMem(jevm).copyFrom(source.getMem(jevm));
        }
    }

    private static class ArithmeticInstruction extends Instruction {
        private final Param p1, p2, dest;
        private final Param.ParamType t1, t2, td;
        private final BinaryOperator<Integer> intFunc;
        private final BinaryOperator<Float> floatFunc;

        public ArithmeticInstruction(Param p1, Param p2, Param dest, BinaryOperator<Integer> intFunc, BinaryOperator<Float> floatFunc){
            t1 = (this.p1 = p1).type;
            t2 = (this.p2 = p2).type;
            td = (this.dest = dest).type;
            this.intFunc = intFunc;
            this.floatFunc = floatFunc;
        }

        @Override
        public void execute(VM jevm) {
            if(!t1.isIntOrFloat()) throw new Error("JeVM Error: Can only add ints and/or floats");
            if(!t2.isIntOrFloat()) throw new Error("JeVM Error: Can only add ints and/or floats");
            if(!td.isIntOrFloat()) throw new Error("JeVM Error: Cannot store ");

            var x1 = (MemoryLocation.M4) p1.getMem(jevm);
            var x2 = (MemoryLocation.M4) p2.getMem(jevm);
            var d = (MemoryLocation.M4) dest.getMem(jevm);

            if(t1.isInt() && t2.isInt() && td.isInt())
                d.setInt(intFunc.apply(x1.getInt(), x2.getInt()));
            else if(t1.isInt() && t2.isFloat() && td.isFloat())
                d.setFloat(floatFunc.apply((float) x1.getInt(), x2.getFloat()));
            else if(t1.isFloat() && t2.isInt() && td.isFloat())
                d.setFloat(floatFunc.apply(x1.getFloat(), (float) x2.getInt()));
            else if(t1.isFloat() && t2.isFloat() && td.isFloat())
                d.setFloat(floatFunc.apply(x1.getFloat(), x2.getFloat()));
            else throw new Error("Invalid parameters to arithmetic operation : (" + t1 + ", " + t2 + ") -> " + td);
        }
    }
    private static class LogicalInstruction extends Instruction {
        private final Param p1, p2, dest;
        private final Param.ParamType t1, t2, td;
        private final BinaryOperator<Integer> intFunc;
        private final BinaryOperator<Boolean> boolFunc;

        public LogicalInstruction(Param p1, Param p2, Param dest, BinaryOperator<Integer> intFunc, BinaryOperator<Boolean> floatFunc){
            t1 = (this.p1 = p1).type;
            t2 = (this.p2 = p2).type;
            td = (this.dest = dest).type;
            this.intFunc = intFunc;
            this.boolFunc = floatFunc;
        }

        @Override
        public void execute(VM jevm) {
            var x1 = p1.getMem(jevm);
            var x2 = p2.getMem(jevm);
            var d = dest.getMem(jevm);

            if(t1.isInt() && t2.isInt() && td.isInt())
                ((MemoryLocation.M4)d) .setInt(intFunc.apply(
                    ((MemoryLocation.M4)x1).getInt(),
                    ((MemoryLocation.M4)x2).getInt()
                ));
            else if(t1.isBool() && t2.isBool() && td.isBool())
                ((MemoryLocation.M1)d) .setBool(boolFunc.apply(
                    ((MemoryLocation.M1)x1).getBool(),
                    ((MemoryLocation.M1)x2).getBool()
                ));
            else throw new Error("Invalid parameters to logical operation : (" + t1 + ", " + t2 + ") -> " + td);
        }
    }


    public class ADD extends ArithmeticInstruction {
        public ADD (Param p1, Param p2, Param dest){
            super(p1, p2, dest, Integer::sum, Float::sum);
        }
    }
    public class SUB extends ArithmeticInstruction {
        public SUB(Param p1, Param p2, Param dest){
            super(p1, p2, dest, (a, b) -> a - b, (a, b) -> a - b);
        }
    }
    public class MUL extends ArithmeticInstruction {
        public MUL(Param p1, Param p2, Param dest){
            super(p1, p2, dest, (a, b) -> a * b, (a, b) -> a * b);
        }
    }
    public class DIV extends ArithmeticInstruction {
        public DIV(Param p1, Param p2, Param dest){
            super(p1, p2, dest, (a, b) -> a / b, (a, b) -> a / b);
        }
    }
    public class MOD extends ArithmeticInstruction {
        public MOD(Param p1, Param p2, Param dest){
            super(p1, p2, dest, (a, b) -> (a % b + b) % b, (a, b) -> (a % b + b) % b);
        }
    }
    public class OR extends LogicalInstruction {
        public OR(Param p1, Param p2, Param dest) {
            super(p1, p2, dest, (a, b) -> a | b, (a, b) -> a || b);
        }
    }

    public abstract void execute(VM jevm);

    public record Param(Function<VM, MemoryLocation> location, ParamType type) {
        public static Param constant(int constVal){
            return new Param($ -> new MemoryLocation.Constant4(constVal), ParamType.INT);
        }
        public static Param constant(float constVal){
            return new Param($ -> new MemoryLocation.Constant4(constVal), ParamType.FLOAT);
        }
        public static Param constant(char constVal){
            return new Param($ -> new MemoryLocation.Constant1(constVal), ParamType.CHAR);
        }
        public static Param constant(boolean constVal){
            return new Param($ -> new MemoryLocation.Constant1(constVal), ParamType.BOOL);
        }
        public static Param register1(int registerId, ParamType type){
            if(!type.isBoolOrChar())
                throw new Error("JeVM Error: Invalid type for 1 byte register");
            return new Param(vm -> vm.getR1(registerId, false), type);
        }
        public static Param register4(int registerId, ParamType type){
            if(!type.isIntOrFloat())
                throw new Error("JeVM Error: Invalid type for 4 byte register");
            return new Param(vm -> vm.getR4(registerId, false), type);
        }
        public static Param mem1(int registerId, ParamType type){
            if(!type.isBoolOrChar())
                throw new Error("JeVM Error: Invalid type for 1 byte memory");
            return new Param(vm -> vm.getR1(registerId, true), type);
        }
        public static Param mem4(int registerId, ParamType type){
            if(!type.isIntOrFloat())
                throw new Error("JeVM Error: Invalid type for 4 byte memory");
            return new Param(vm -> vm.getR4(registerId, true), type);
        }

        public enum ParamType {
            FLOAT, INT, CHAR, BOOL;
            public boolean isInt(){ return this == INT; }
            public boolean isFloat(){ return this == FLOAT; }
            public boolean isBool(){ return this == BOOL; }
            public boolean isChar(){ return this == CHAR; }
            public boolean isIntOrFloat() { return isInt() || isFloat(); }
            public boolean isBoolOrChar() { return isBool() || isChar(); }
            public boolean isIntOrBool() { return isInt() || isBool(); }
        }

        public MemoryLocation getMem(VM vm) {
            return location().apply(vm);
        }
    }
}
