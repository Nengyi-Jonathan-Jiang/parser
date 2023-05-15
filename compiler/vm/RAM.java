package compiler.vm;

import java.nio.ByteOrder;
import java.util.Arrays;

public class RAM {
    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private Memory stack, heap;

    private static class Memory {
        private static final int MAX_MEMORY = 1 << 24;

        private byte[] memory = new byte[65536];
        private int used = 0;

        private byte get(int i) {
            if(i < 0)
                throw new Error("JeVM error: Cannot access negative memory");
            if(i >= MAX_MEMORY)
                throw new Error("JeVM error: Out of memory (" + i + " exceeds the maximum allowed memory of " + MAX_MEMORY + " bytes)");

            while (i >= used){
                used *= 2;
                memory = Arrays.copyOf(memory, used);
            }

            return memory[i];
        }
    }

    private byte _get(int i){
        return i < 0 ? heap.get(-(i + 1)) : stack.get(i);
    }

    public byte getByte(int i){
        return _get(i);
    }
    public char getChar(int i){
        return (char) (_get(i) & 0xFF);
    }
    public boolean getBool(int i){
        return _get(i) != 0;
    }
    public int getInt(int i){
        return _get(i) | _get(i + 1) << 8 | _get(i + 2) << 16 | _get(i + 3) << 24;
    }
    public float getFloat(int i){
        return Float.intBitsToFloat(_get(i) | _get(i + 1) << 8 | _get(i + 2) << 16 | _get(i + 3) << 24);
    }
}