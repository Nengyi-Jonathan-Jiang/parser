package compiler.jevm;

import java.util.Arrays;
import java.util.Map;

class Memory {
    private static final int MAX_MEMORY = 1 << 16;

    private byte[] memory = new byte[65536];
    private int used = 0;

    private void resizeToFit(int ptr) {
        if (ptr < 0)
            throw new Error("JeVM error: Cannot access negative memory");
        if (ptr >= MAX_MEMORY)
            throw new Error("JeVM error: Out of memory (" + ptr + " exceeds the maximum allowed memory of " + MAX_MEMORY + " bytes)");

        used = Math.max(used, ptr + 1);

        while (ptr >= memory.length) {
            memory = Arrays.copyOf(memory, memory.length * 2);
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

    public void dumpContents (){
        for(int i = 0; i < used; i ++){
            System.out.print(Integer.toString(256 + (((int) memory[i]) & 0xFF), 16).substring(1) + " ");
            if((i & 3) == 3) System.out.print(" ");
            if((i & 15) == 15) System.out.println();
        }
    }
}