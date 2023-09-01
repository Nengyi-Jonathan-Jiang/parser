package jpp.jevm;

import compiler.util.Pair;

import java.util.*;

public class Allocator {
    private final Map<Integer, Integer> sizes = new TreeMap<>();
    private final TreeMap<Integer, Set<Integer>> freeBlocks = new TreeMap<>();

    private final TreeMap<Integer, Integer> freeBlockBefore = new TreeMap<>();
    private final TreeMap<Integer, Integer> freeBlockSize = new TreeMap<>();

    private int used = 0;

    private int useFreeBlock(int size) {
        Set<Integer> blocks = freeBlocks.get(size);

        //noinspection OptionalGetWithoutIsPresent
        int ptr = blocks.stream().findFirst().get();

        removeFreeBlock(size, ptr);

        return ptr;
    }

    private void returnBlock(int ptr, int size) {
        freeBlocks.computeIfAbsent(size, $ -> new TreeSet<>()).add(ptr);
        freeBlockBefore.put(ptr + size, ptr);
        freeBlockSize.put(ptr, size);

        // Merge with block before
        var ptrBefore = freeBlockBefore.get(ptr);
        if(ptrBefore != null){
            var sizeBefore = freeBlockSize.get(ptrBefore);

            var merge = mergeFreeBlocks(ptrBefore, sizeBefore, ptr, size);
            ptr = merge.a();
            size = merge.b();
        }

        // Merge with block after
        var sizeAfter = freeBlockSize.get(ptr + size);
        if(sizeAfter != null){
            var ptrAfter = ptr + size;

            mergeFreeBlocks(ptr, size, ptrAfter, sizeAfter);
        }
    }

    private Pair<Integer, Integer> mergeFreeBlocks(int p1, int s1, int p2, int s2) {
        freeBlockSize.put(p1, s1 + s2);
        freeBlockSize.remove(p2);
        freeBlockBefore.remove(p2);
        freeBlockBefore.put(p2 + s2, p1);

        removeFreeBlock(s1, p1);
        removeFreeBlock(s2, p2);

        freeBlocks.computeIfAbsent(s1 + s2, $ -> new TreeSet<>()).add(p1);

        return new Pair<>(p1, s1 + s2);
    }

    private void removeFreeBlock(int size, int ptr){
        Set<Integer> blocks = freeBlocks.get(size);
        blocks.remove(ptr);
        if(blocks.size() == 0) freeBlocks.remove(size);
    }

    public int allocate(int allocSize){
        int ptr;

        // Try to find a free block that works
        Integer availableSize = freeBlocks.higherKey(allocSize - 1);

        if(availableSize != null){  // We found a block that works
            // Get the location of the block
            ptr = useFreeBlock(availableSize);

            // Split the block
            if(availableSize > allocSize) returnBlock(ptr + allocSize, availableSize - allocSize);
        }
        else {
            // Allocate a new space at the end of the heap
            ptr = used;
            used += allocSize;
        }

        // Update sizes map
        sizes.put(ptr, allocSize);
        return ptr;
    }

    public void deallocate(int ptr){
        if(!isValidPointer(ptr)) throw new Error("JeVM Error: Segmentation fault");

        int size = sizes.get(ptr);
        sizes.remove(ptr);
        returnBlock(ptr, size);
    }

    public boolean isValidPointer(int ptr){
        return sizes.containsKey(ptr);
    }

    public void debug(){
        System.out.println(sizes);
        System.out.println(freeBlocks);
    }
}