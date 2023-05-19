package compiler.jevm;

public interface MemoryLocation {
    interface M1 extends MemoryLocation {
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

    interface M4 extends MemoryLocation  {
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

    interface Constant extends MemoryLocation {}

    class Constant1 implements M1, Constant {
        private final byte value;
        public Constant1(char c){
            value = charToByte(c);
        }
        public Constant1(boolean b){
            value = boolToByte(b);
        }

        @Override
        public void setChar(char c) {
            throw new IllegalAccessError("JeVM Error: Cannot write to a constant");
        }

        @Override
        public char getChar() {
            return byteToChar(value);
        }

        @Override
        public void setBool(boolean b) {
            throw new IllegalAccessError("JeVM Error: Cannot write to a constant");
        }

        @Override
        public boolean getBool() {
            return byteToBool(value);
        }
    }

    class Constant4 implements M4, Constant {
        // We don't have to store a const4 as a byte array because we only need to expose getInt and getFloat

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


    default boolean byteToBool(byte b1) {
        return b1 == 0;
    }
    default byte boolToByte(boolean val){
        return (byte)(val ? 0 : 1);
    }
    default char byteToChar(byte b1) {
        return (char)(b1 & 0xFF);
    }
    default byte charToByte(char val) {
        return (byte)(val & 0xFF);
    }
    default int bytesToInt(byte b1, byte b2, byte b3, byte b4){
        return (b1 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 |(b4 & 0xFF) << 24;
    }
    default byte[] intToBytes(int val){
        return new byte[]{(byte) (val & 0xFF), (byte)(val >> 8 & 0xFF), (byte)(val >> 16 & 0xFF), (byte)(val >> 24 & 0xFF)};
    }
    default float bytesToFloat(byte b1, byte b2, byte b3, byte b4) {
        return Float.intBitsToFloat(bytesToInt(b1, b2, b3, b4));
    }
    default byte[] floatToBytes(float val) {
        return intToBytes(Float.floatToRawIntBits(val));
    }
}