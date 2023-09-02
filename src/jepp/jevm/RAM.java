package jepp.jevm;

public class RAM {
    private final Memory stack = new Memory(), heap = new Memory();

    private int _get(int ptr){
        return ptr < 0 ? heap.get(-(ptr + 1)) : stack.get(ptr);
    }
    private void _set(int ptr, int value){
        if (ptr < 0)
            heap.set(-(ptr + 1), value);
        else
            stack.set(ptr, value);
    }

    public MemoryLocation get(int ptr){
        return new MemoryLocation() {
            public int getInt(){
                return _get(ptr);
            }
            public void setInt(int val) {
                _set(ptr, val);
            }
            public float getFloat(){
                return intToFloat(_get(ptr));
            }
            public void setFloat(float val) {
                _set(ptr, floatToInt(val));
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