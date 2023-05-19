#pragma once

#include <map>
#include <set>
#include "jevm.h"

class Allocator {
    std::map<jevm_ptr, jevm_size_t> allocatedSizes, freeBlockSize;
    std::map<jevm_size_t, std::set<jevm_ptr>> freeBlocks;
    std::map<jevm_ptr, jevm_ptr> freeBlockBefore;

    int used = 0;

    int useFreeBlock(int size) {
        std::set<jevm_ptr>& blocks = freeBlocks[size];
        jevm_ptr ptr = *blocks.begin();
        removeFreeBlock(size, ptr);
        return ptr;
    }

    void returnBlock(jevm_ptr ptr, jevm_size_t size) {
        freeBlocks[size].emplace(ptr);
        freeBlockBefore[ptr + (int)size] = ptr;
        freeBlockSize[ptr + (int)size] = ptr;

        // Merge with block before
        if (freeBlockSize.contains(ptr)) {
            jevm_ptr ptrBefore = freeBlockBefore[ptr];
            jevm_size_t sizeBefore = freeBlockSize[ptrBefore];
            
            auto [_ptr, _size] = mergeFreeBlocks(ptrBefore, sizeBefore, ptr, size);
            ptr = _ptr;
            size = _size;
        }

        // Merge with block after
        if (freeBlockSize.contains(ptr + (int) size)) {
            jevm_size_t sizeAfter = freeBlockSize[ptr + (int) size];
            jevm_ptr ptrAfter = ptr + (int) size;

            mergeFreeBlocks(ptr, size, ptrAfter, sizeAfter);
        }
    }

    std::pair<int, int> mergeFreeBlocks(jevm_ptr p1, jevm_size_t s1, jevm_ptr p2, jevm_size_t s2) {
        freeBlockSize.erase(p2);
        freeBlockBefore.erase(p2);
        freeBlockSize[p1] = s1 + s2;
        freeBlockBefore[p2 + (int) s2] = p1;

        removeFreeBlock(s1, p1);
        removeFreeBlock(s2, p2);

        freeBlocks[s1 + s2].emplace(p1);

        return {p1, s1 + s2};
    }

    void removeFreeBlock(jevm_size_t size, jevm_ptr ptr) {
        std::set<int> blocks = freeBlocks[size];
        blocks.erase(ptr);
        if (blocks.empty()) freeBlocks.erase(size);
    }

    int allocate(int allocSize) {
        int ptr;

        // Try to find a free block that works
        size_t availableSize = freeBlocks.upper_bound(allocSize - 1)->first;

        if (availableSize != null) {  // We found a block that works
            // Get the location of the block
            ptr = useFreeBlock(availableSize);

            // Split the block
            if (availableSize > allocSize) returnBlock(ptr + allocSize, availableSize - allocSize);
        } else {
            // Allocate a new space at the end of the heap
            ptr = used;
            used += allocSize;
        }

        // Update allocatedSizes map
        allocatedSizes.put(ptr, allocSize);
        return ptr;
    }

    void deallocate(int ptr) {
        if (!isValidPointer(ptr)) throw new Error("JeVM Error: Segmentation fault");

        int size = allocatedSizes.get(ptr);
        allocatedSizes.remove(ptr);
        returnBlock(ptr, size);
    }

    boolean isValidPointer(int ptr) {
        return allocatedSizes.containsKey(ptr);
    }

    void debug() {
        System.out.println(allocatedSizes);
        System.out.println(freeBlocks);
    }
}