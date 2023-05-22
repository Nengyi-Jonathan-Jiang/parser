#pragma once

#include "jevm.h"
#include "memory.h"
#include <cmath>

enum type { BOOL, t_C, t_I, t_F };

struct instruction {
    virtual void execute(jevm& vm) = 0;
};

struct instructionParam {
    const type t;
    explicit instructionParam(type t) : t(t) {}
    virtual loc get(jevm& vm) const = 0;
};


typedef const instructionParam* const jParam;

struct constantParam : instructionParam {
    constant c;

    constantParam(bool b) : instructionParam(type::BOOL), c(b) {}   // NOLINT(google-explicit-constructor)
    constantParam(char c) : instructionParam(type::t_C), c(c) {}   // NOLINT(google-explicit-constructor)
    constantParam(int i) : instructionParam(type::t_I), c(i) {}     // NOLINT(google-explicit-constructor)
    constantParam(float f) : instructionParam(type::t_F), c(f) {} // NOLINT(google-explicit-constructor)

    loc get(jevm&) const override {
        return dynamic_cast<loc>(&c);
    }
};
struct regParam : public instructionParam {
    int registerID;

    regParam(int registerID, type t) : instructionParam(t), registerID(registerID) {}

    loc get(jevm& vm) const override {
        return vm.getRegister(registerID);
    }
};
struct memParam : public instructionParam {
    int registerID;

    memParam(int registerID, type t) : instructionParam(t), registerID(registerID) {}

    loc get(jevm& vm) const override {
        return vm.getMemory(registerID);
    }
};

namespace instructions {
    struct INP : public instruction {
        jParam param;

        INP(jParam  param) : param(param) {}

        void execute(jevm &vm) override {
            vm.read(param->t, param->get(vm));
        }
    };
    struct DSP : public instruction {
        jParam  param;

        DSP(jParam param) : param(param) {}

        void execute(jevm &vm) override {
            vm.display(param->t, param->get(vm));
        }
    };

    struct JMP : public instruction {
        enum condition { GZ = 1, LZ = 2, EZ = 4 };

        jParam var, dest;
        int condition;

        JMP (jParam var, jParam dest, int condition) : var(var), dest(dest), condition(condition) {}

        void execute(jevm &vm) override {
            loc x = var->get(jevm);
            type t = var->t;

            float f;
            int comp = 0;
            switch (t) {
                case t_F:
                    f = x->getFloat();
                    comp = f < 0 ? -1 : f > 0 ? 1 : 0;
                    break;
                case t_I:
                    comp = x->getInt();
                    break;
                default: return;
            }

            bool res = false;
            if((condition & GZ) != 0) res |= comp > 0;
            if((condition & LZ) != 0) res |= comp < 0;
            if((condition & EZ) != 0) res |= comp == 0;

            if (res) vm.jump(dest->get(jevm).getInt());
        }
    };

    template<class T, T (*i)(T, T), type t>
    struct arithmeticInstruction : public instruction {
        jParam p1, p2, dest;
        void(*func)(loc, loc, loc);

        arithmeticInstruction(jParam p1, jParam p2, jParam dest) : p1(p1), p2(p2), dest(dest) {
            func = p1->t == t && p2->t == t && dest->t == t
                ? [](loc x1, loc x2, loc d) {d->set(i(x1->getInt(), x2->getInt()));}
                : [](loc a, loc b, loc c) {};
        }
        
        void execute(jevm &vm) override {
            func(p1->get(vm), p2->get(vm), dest->get(vm));
        }
    };
    
    typedef arithmeticInstruction<jevm_i, [](jevm_i a, jevm_i b){ return a + b; }, t_I> ADDi;
    typedef arithmeticInstruction<jevm_f, [](jevm_f a, jevm_f b){ return a + b; }, t_F> ADDf;
    typedef arithmeticInstruction<jevm_i, [](jevm_i a, jevm_i b){ return a - b; }, t_I> SUBi;
    typedef arithmeticInstruction<jevm_f, [](jevm_f a, jevm_f b){ return a - b; }, t_F> SUBf;
    typedef arithmeticInstruction<jevm_i, [](jevm_i a, jevm_i b){ return a * b; }, t_I> MULi;
    typedef arithmeticInstruction<jevm_f, [](jevm_f a, jevm_f b){ return a * b; }, t_F> MULf;
    typedef arithmeticInstruction<jevm_i, [](jevm_i a, jevm_i b){ return a / b; }, t_I> DIVi;
    typedef arithmeticInstruction<jevm_f, [](jevm_f a, jevm_f b){ return a / b; }, t_F> DIVf;
    typedef arithmeticInstruction<jevm_i, [](jevm_i a, jevm_i b){ return a % b; }, t_I> MODi;
    typedef arithmeticInstruction<jevm_f, std::fmod, t_F> MODf;

    struct SHR : public instruction {

    };
}