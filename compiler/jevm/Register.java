package compiler.jevm;

public class Register {
    private Register(){}

    public static class R1 implements MemoryLocation.M1 {
        protected byte value;
        public void setChar(char c) {
            value = (byte) (c & 0xFF);
        }
        public char getChar() {
            return (char) (value & 0xFF);
        }
        public void setBool(boolean b) {
            value = (byte) (b ? 1 : 0);
        }
        public boolean getBool() {
            return value != 0;
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