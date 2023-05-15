package compiler.vm;

public class RAM {
    private Memory stack, heap;

    private byte _get(int ptr){
        return ptr < 0 ? heap.get(-(ptr + 1)) : stack.get(ptr);
    }
    private void _set(int ptr, byte value){
        if (ptr < 0)
            heap.set(-(ptr + 1), value);
        else
            stack.set(ptr, value);
    }

    public char getChar(int ptr){
        return (char) (_get(ptr) & 0xFF);
    }
    public void setChar(int ptr, char c){
        _set(ptr, (byte)(c & 0xFF));
    }
    public boolean getBool(int ptr){
        return _get(ptr) != 0;
    }
    public void setBool(int ptr, boolean b) {
        _set(ptr, (byte)(b ? 1 : 0));
    }
    public int getInt(int ptr){
        return _get(ptr) | _get(ptr + 1) << 8 | _get(ptr + 2) << 16 | _get(ptr + 3) << 24;
    }
    public void setInt(int ptr, int val) {
        _set(ptr, (byte)(val & 0xFF));
        _set(ptr + 1, (byte)(val >> 8 & 0xFF));
        _set(ptr + 2, (byte)(val >> 16 & 0xFF));
        _set(ptr + 3, (byte)(val >> 24 & 0xFF));
    }
    public float getFloat(int ptr){
        return Float.intBitsToFloat(getInt(ptr));
    }
    public void setFloat(int ptr, float val) {
        setInt(ptr, Float.floatToRawIntBits(val));
    }
}