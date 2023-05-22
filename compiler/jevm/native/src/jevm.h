#pragma once

#include <cstddef>
#include <array>
#include <iostream>
#include "memory.h"
#include "instruction.h"

typedef int jevm_ptr;
typedef bool  jevm_b;
typedef char  jevm_c;
typedef int   jevm_i;
typedef float jevm_f;
typedef unsigned char jevm_byte;

typedef unsigned jevm_size_t;

typedef std::array<jevm_byte, 4> jevm_byte4;

struct jevm {
    reg r[10];
    RAM ram;
    int instructionPtr = 0;

    jevm() = default;

    memoryLocation* getRegister(int id) {
        return &r[id + 2];
    }

    memoryLocation* getMemory(int id) {
        return ram[getRegister(id)->getInt()];
    }

    void jump(int dest) {
        instructionPtr = dest - 1;
    }

    void read(const type t, memoryLocation *loc) {  // NOLINT(readability-convert-member-functions-to-static)
        bool b; char c; int i; float f;
        switch(t) {
            case BOOL:
                std::cin >> b;
                loc->set(b);
                break;
            case t_C:
                std::cin >> c;
                loc->set(c);
                break;
            case t_I:
                std::cin >> i;
                loc->set(i);
                break;
            case t_F:
                std::cin >> f;
                loc->set(f);
                break;
        }
    }

    void display(const type t, memoryLocation *loc) { // NOLINT(readability-convert-member-functions-to-static)
        switch(t) {
            case BOOL:
                std::cout << loc->getBool();
                break;
            case t_C:
                std::cout << loc->getChar();
                break;
            case t_I:
                std::cout << loc->getInt();
                break;
            case t_F:
                std::cout << loc->getFloat();
                break;
        }
    }
};