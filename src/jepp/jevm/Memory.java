package jepp.jevm;

import java.util.Arrays;

class Memory {
    private static final int INITIAL_MEMORY = 1 << 8;
    private static final int MAX_MEMORY = 1 << 16;

    private int[] memory = new int[INITIAL_MEMORY];

    private void resizeToFit(int ptr) {
        if (ptr < 0)
            throw new Error("JeVM error: Cannot access negative memory");
        if (ptr >= MAX_MEMORY)
            throw new Error("JeVM error: Out of memory (" + ptr + " exceeds the maximum allowed memory of " + MAX_MEMORY + " bytes)");

        while (ptr >= memory.length)
            memory = Arrays.copyOf(memory, memory.length * 2);
    }

    public int get(int ptr) {
        resizeToFit(ptr);
        return memory[ptr];
    }

    public void set(int ptr, int value) {
        resizeToFit(ptr);
        memory[ptr] = value;
    }

    public void dumpContents (){
        for(int i = 0; i < memory.length; i ++){
            String s = Integer.toString(memory[i], 16);
            s = "0".repeat(8 - s.length()) + s;
            System.out.print(s + " ");
            if((i & 3) == 3) System.out.println();
        }
    }
}