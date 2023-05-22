#pragma once

#include <cstddef>
#include <array>

typedef int jevm_ptr;
typedef bool  jevm_b;
typedef char  jevm_c;
typedef int   jevm_i;
typedef float jevm_f;
typedef unsigned char jevm_byte;

typedef unsigned jevm_size_t;

typedef std::array<jevm_byte, 4> jevm_byte4;

struct jevm {

};