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
                return byteToChar(_get(ptr));
            }
            public void setChar(char c){
                _set(ptr, charToByte(c));
            }
            public boolean getBool(){
                return byteToBool(_get(ptr));
            }
            public void setBool(boolean b) {
                _set(ptr, boolToByte(b));
            }
        };
    }

    public MemoryLocation.M4 getM4(int ptr){
        return new MemoryLocation.M4() {
            public int getInt(){
                return bytesToInt(_get(ptr), _get(ptr + 1), _get(ptr + 2), _get(ptr + 3));
            }
            public void setInt(int val) {
                byte[] bytes = intToBytes(val);
                _set(ptr, bytes[0]);
                _set(ptr + 1, bytes[1]);
                _set(ptr + 2, bytes[2]);
                _set(ptr + 3, bytes[3]);
            }
            public float getFloat(){
                return bytesToFloat(_get(ptr), _get(ptr + 1), _get(ptr + 2), _get(ptr + 3));
            }
            public void setFloat(float val) {
                byte[] bytes = floatToBytes(val);
                _set(ptr, bytes[0]);
                _set(ptr + 1, bytes[1]);
                _set(ptr + 2, bytes[2]);
                _set(ptr + 3, bytes[3]);
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

    public void dumpContents() {
        stack.dumpContents();
    }
}