package compiler.jevm;

import java.util.Arrays;

class Memory {
    private static final int MAX_MEMORY = 1 << 8;

    private byte[] memory = new byte[65536];
    private int used = 0;

    private void resizeToFit(int ptr) {
        if (ptr < 0)
            throw new Error("JeVM error: Cannot access negative memory");
        if (ptr >= MAX_MEMORY)
            throw new Error("JeVM error: Out of memory (" + ptr + " exceeds the maximum allowed memory of " + MAX_MEMORY + " bytes)");

        while (ptr >= used) {
            if(used < 256)
                used += 16;
            else
                used *= 2;
            memory = Arrays.copyOf(memory, used);
        }
    }

    public byte get(int ptr) {
        resizeToFit(ptr);
        return memory[ptr];
    }

    public void set(int ptr, byte b) {
        resizeToFit(ptr);
        memory[ptr] = b;
    }
}