package compiler.jevm;

public class Register implements MemoryLocation {
    protected int value;
    public void setInt(int val) {
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