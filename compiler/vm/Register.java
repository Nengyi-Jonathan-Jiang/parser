package compiler.vm;

public class Register {
    private Register(){}

    public static class Reg1 {
        private byte value;
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

    public static class Reg4 {
        private final byte[] bytes = new byte[4];
        public void setInt(int val){
            bytes[0] = (byte)(val & 0xFF);
            bytes[1] = (byte)(val >> 8 & 0xFF);
            bytes[2] = (byte)(val >> 16 & 0xFF);
            bytes[3] = (byte)(val >> 24 & 0xFF);
        }
        public void setFloat(float val){
            setInt(Float.floatToRawIntBits(val));
        }
        public int getInt(){
            return bytes[0] | bytes[1] << 8 | bytes[2] << 16 | bytes[3] << 24;
        }

        public float getFloat() {
            return Float.intBitsToFloat(getInt());
        }
    }
}