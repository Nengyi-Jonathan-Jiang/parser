#pragma once

#include "jevm.h"

struct instruction {
    virtual void execute(jevm& vm) = 0;
};

struct instructionParam {
    virtual void get(jevm& vm) = 0;
};

namespace instructions {
    struct INP : instruction {

    };
}