package compiler.jevm;

public interface MemoryLocation {
    public interface M1 extends MemoryLocation {
        void setChar(char c);
        char getChar();
        void setBool(boolean b);
        boolean getBool();

        @Override
        default void copyFrom(MemoryLocation location) {
            if(location instanceof M1) {
                setChar(((M1) location).getChar());
                return;
            }
            if(location instanceof M4) {
                setChar((char)(((M4) location).getInt() & 0xFF));
                return;
            }
            throw new IllegalAccessError("JeVM Error: Invalid memory to copy from");
        }
    }

    public interface M4 extends MemoryLocation  {
        void setInt(int val);
        void setFloat(float val);
        int getInt();
        float getFloat();

        @Override
        default void copyFrom(MemoryLocation location) {
            if(location instanceof M1) {
                setInt(((M1) location).getChar() & 0xFF);
                return;
            }
            if(location instanceof M4) {
                setInt(((M4) location).getInt());
                return;
            }
            throw new IllegalAccessError("JeVM Error: Invalid memory to copy from");
        }
    }

    public interface Constant extends MemoryLocation {}

    public static class Constant1 implements M1, Constant {
        private final byte value;
        public Constant1(char c){
            value = (byte) (c & 0xFF);
        }
        public Constant1(boolean b){
            value = (byte)(b ? 1 : 0);
        }

        @Override
        public void setChar(char c) {
            throw new IllegalAccessError("JeVM Error: Cannot write to a constant");
        }

        @Override
        public char getChar() {
            return (char)(value & 0xFF);
        }

        @Override
        public void setBool(boolean b) {
            throw new IllegalAccessError("JeVM Error: Cannot write to a constant");
        }

        @Override
        public boolean getBool() {
            return value != 0;
        }
    }

    public static class Constant4 implements M4, Constant {
        int value;
        public Constant4(int value) {
            this.value = value;
        }
        public Constant4(float value){
            this.value = Float.floatToRawIntBits(value);
        }

        @Override
        public void setInt(int val) {
            throw new IllegalAccessError("JeVM Error: Cannot write to a constant");
        }

        @Override
        public void setFloat(float val) {
            throw new IllegalAccessError("JeVM Error: Cannot write to a constant");
        }

        @Override
        public int getInt() {
            return value;
        }

        @Override
        public float getFloat() {
            return Float.intBitsToFloat(value);
        }
    }

    void copyFrom(MemoryLocation location);
}