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
    virtual void set(bool b) = 0;
    virtual void set(char c) = 0;
    virtual void set(int i) = 0;
    virtual void set(float f) = 0;
    virtual jevm_b getBool() = 0;
    virtual jevm_c getChar() = 0;
    virtual jevm_i getInt() = 0;
    virtual jevm_f getFloat() = 0;

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

typedef memoryLocation* loc;

struct RAM {
    memory stack, heap;

    jevm_byte& operator[](jevm_ptr ptr) {
        return ptr < 0 ? heap[-(ptr + 1)] : stack[ptr];
    }
};

class constant : public memoryLocation {
    const union data {
        jevm_byte data1; jevm_byte4 data4;
        data(jevm_byte data) : data1(data) {}
        data(jevm_byte4 data) : data4(data) {}
    } data;

public:
    constant(jevm_b b) : data(boolToByte(b))   {} // NOLINT(google-explicit-constructor)
    constant(jevm_c c) : data(charToByte(c))   {} // NOLINT(google-explicit-constructor)
    constant(jevm_i i) : data(intToBytes(i))   {} // NOLINT(google-explicit-constructor)
    constant(jevm_f f) : data(floatToBytes(f)) {} // NOLINT(google-explicit-constructor)

    void set(jevm_b b) override {}
    void set(jevm_c c) override {}
    void set(jevm_i i) override {}
    void set(jevm_f f) override {}

    jevm_b getBool()  override { return byteToBool(data.data1); };
    jevm_c getChar()  override { return byteToChar(data.data1); };
    jevm_i getInt()   override { return bytesToInt(data.data4); };
    jevm_f getFloat() override { return bytesToFloat(data.data4); };
};

class reg : public memoryLocation {
    union data {
        jevm_byte data1; jevm_byte4 data4;
        data(jevm_byte data) : data1(data) {}
        data(jevm_byte4 data) : data4(data) {}
    } data;

public:
    reg(bool b)  : data(boolToByte(b)) {}
    reg(char c)  : data(charToByte(c)) {}
    reg(int i)   : data(intToBytes(i)) {}
    reg(float f) : data(floatToBytes(f)) {}

    void set(jevm_b b)  override { data.data1 = boolToByte(b); }
    void set(jevm_c c)  override { data.data1 = charToByte(c); }
    void set(jevm_i i)   override { data.data4 = intToBytes(i); }
    void set(jevm_f f) override { data.data4 = floatToBytes(f); }
    jevm_b getBool()  override { return byteToBool(data.data1); }
    jevm_c getChar()  override { return byteToChar(data.data1); }
    jevm_i getInt()   override { return bytesToInt(data.data4); }
    jevm_f getFloat() override { return bytesToFloat(data.data4); }
};