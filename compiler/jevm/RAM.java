package compiler.jevm;

public class RAM {
    private final Memory stack = new Memory(), heap = new Memory();

    private byte _get(int ptr){
        return ptr < 0 ? heap.get(-(ptr + 1)) : stack.get(ptr);
    }
    private void _set(int ptr, byte value){
        if (ptr < 0)
            heap.set(-(ptr + 1), value);
        else
            stack.set(ptr, value);
    }

    public MemoryLocation.M1 getM1(int ptr){
        return new MemoryLocation.M1() {
            public char getChar(){
                return (char) (_get(ptr) & 0xFF);
            }
            public void setChar(char c){
                _set(ptr, (byte)(c & 0xFF));
            }
            public boolean getBool(){
                return _get(ptr) != 0;
            }
            public void setBool(boolean b) {
                _set(ptr, (byte)(b ? 1 : 0));
            }
        };
    }

    public MemoryLocation.M4 getM4(int ptr){
        return new MemoryLocation.M4() {
            public int getInt(){
                return _get(ptr) | _get(ptr + 1) << 8 | _get(ptr + 2) << 16 | _get(ptr + 3) << 24;
            }
            public void setInt(int val) {
                _set(ptr, (byte)(val & 0xFF));
                _set(ptr + 1, (byte)(val >> 8 & 0xFF));
                _set(ptr + 2, (byte)(val >> 16 & 0xFF));
                _set(ptr + 3, (byte)(val >> 24 & 0xFF));
            }
            public float getFloat(){
                return Float.intBitsToFloat(getInt());
            }
            public void setFloat(float val) {
                setInt(Float.floatToRawIntBits(val));
            }
        };
    }

    public void memcpy(int source, int dest, int length){
        if(source < 0 && source + length > 0 || dest < 0 && dest + length > 0 || source <= dest && source + length > dest || dest <= source && dest + length > source){
            throw new Error("JeVM Error: Invalid copy range");
        }
        for(int i = 0; i < length; i++){
            _set(dest + i, _get(source + i));
        }
    }
}