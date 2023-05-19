#pragma once

#include <cstring>
#include "jevm.h"

struct memory {
    memory() {
        data = new jevm_byte [used = INITIAL_SIZE];
    }
    ~memory() {
        delete[] data;
    }

    memory(const memory&) = delete;
    void operator=(const memory&) = delete;

    jevm_byte& operator[](jevm_ptr ptr) {
        while(ptr >= used && used < MAX_MEMORY) {
            auto* newData = new jevm_byte [used * 2];
            memcpy(newData, data, used);
            delete[] data;
            used *= 2;
        }
        if(ptr < 0 || ptr >= MAX_MEMORY) return garbage;
        return data[ptr];
    }

private:
    jevm_byte* data;
    jevm_size_t used;

    const static jevm_size_t MAX_MEMORY = 1 << 16;
    const static jevm_size_t INITIAL_SIZE = 256;

    static jevm_byte garbage;
};

struct memoryLocation {
    virtual memoryLocation& operator=(bool b) = 0;
    virtual memoryLocation& operator=(char c) = 0;
    virtual memoryLocation& operator=(int i) = 0;
    virtual memoryLocation& operator=(float f) = 0;
    virtual explicit operator jevm_b() = 0;
    virtual explicit operator jevm_c() = 0;
    virtual explicit operator jevm_i() = 0;
    virtual explicit operator jevm_f() = 0;

protected:
    static jevm_byte boolToByte(jevm_b b){
        return static_cast<jevm_byte>(b);
    }
    static jevm_b byteToBool(jevm_byte data){
        return static_cast<jevm_b>(data);
    }
    static jevm_byte charToByte(jevm_c c){
        return static_cast<jevm_byte>(c);
    }
    static jevm_c byteToChar(jevm_byte data){
        return static_cast<jevm_c>(data);
    }
    static jevm_byte4  intToBytes(jevm_i i){
        return {
            static_cast<jevm_byte>(i & 0xFF),
            static_cast<jevm_byte>((i >> 8) & 0xFF),
            static_cast<jevm_byte>((i >> 16) & 0xFF),
            static_cast<jevm_byte>((i >> 24) & 0xFF)
        };
    }
    static int bytesToInt(jevm_byte4  data){
        return data[0] | (data[1] << 8) | (data[2] << 16) | (data[3] << 24);
    }
    static jevm_byte4  floatToBytes(jevm_f f){
        return intToBytes(*((int*)&f)); // Unholy magic
    }
    static float bytesToFloat(jevm_byte4 data) {
        int res = bytesToInt(data);
        return *((float*)&res); // Unholy magic
    }
};

struct RAM {
    memory stack, heap;

private:
    jevm_byte& operator[](jevm_ptr ptr) {
        return ptr < 0 ? heap[-(ptr + 1)] : stack[ptr];
    }
};

class constant : memoryLocation {
    const union data {
        jevm_byte data1; jevm_byte4 data4;
        data(jevm_byte data) : data1(data) {}
        data(jevm_byte4 data) : data4(data) {}
    } data;
    constant(bool b)  : data(boolToByte(b)) {}
    constant(char c)  : data(charToByte(c)) {}
    constant(int i)   : data(intToBytes(i)) {}
    constant(float f) : data(floatToBytes(f)) {}

    memoryLocation& operator=(bool b)  override { return *this; }
    memoryLocation& operator=(char c)  override { return *this; }
    memoryLocation& operator=(int i)   override { return *this; };
    memoryLocation& operator=(float f) override { return *this; };

    explicit operator jevm_b() override { return byteToBool(data.data1); };
    explicit operator jevm_c() override { return byteToChar(data.data1); };
    explicit operator jevm_i() override { return bytesToInt(data.data4); };
    explicit operator jevm_f() override { return bytesToFloat(data.data4); };
};