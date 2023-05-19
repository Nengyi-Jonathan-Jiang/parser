package compiler.jevm;

public class Register {
    private Register(){}

    public static class R1 implements MemoryLocation.M1 {
        protected byte value;
        public void setChar(char c) {
            value = charToByte(c);
        }
        public char getChar() {
            return byteToChar(value);
        }
        public void setBool(boolean b) {
            value = boolToByte(b);
        }
        public boolean getBool() {
            return byteToBool(value);
        }
    }

    public static class R4 implements MemoryLocation.M4 {
        protected int value;
        public void setInt(int val){
            value = val;
        }
        public void setFloat(float val){
            setInt(Float.floatToRawIntBits(val));
        }
        public int getInt(){
            return value;
        }
        public float getFloat() {
            return Float.intBitsToFloat(getInt());
        }
    }
}