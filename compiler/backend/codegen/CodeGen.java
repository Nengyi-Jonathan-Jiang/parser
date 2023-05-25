package compiler.backend.codegen;

import java.util.Arrays;

public class CodeGen {
    enum sectionType {
        custom(0), type(1), imprt(2), func(3), table(4), memory(5), global(6),
        export(7), start(8), element(9), code(10), data(11);

        public final byte enc;
        sectionType(int x) {
            enc = (byte) x;
        }
    }

    enum valueType {
        i32(0x7f), f32(0x7d);

        public final byte enc;
        valueType(int x){
            enc = (byte) x;
        }
    }

    enum blockType {
        VOID(0x40);

        public final byte enc;
        blockType(int x) {
            enc = (byte) x;
        }
    }

    enum opcodes {
        block(0x02),
        loop(0x03),
        br(0x0c),
        br_if(0x0d),
        end(0x0b),
        call(0x10),

        get_local(0x20),
        set_local(0x21),

        i32_load(0x28),
        f32_load(0x2A),
        i32_store(0x36),
        f32_store(0x38),

        mem_size(0x3F),
        mem_grow(0x40),

        i32_store_8(0x3a),

        i32_const(0x41),
        f32_const(0x43),

        i32_eqz(0x45),
        i32_eq(0x46),
        i32_ne(0x47),
        i32_lt_s(0x48),
        i32_lt_u(0x49),
        i32_gt_s(0x4a),
        i32_gt_u(0x4b),
        i32_le_s(0x4c),
        i32_le_u(0x4d),
        i32_ge_s(0x4e),
        i32_ge_u(0x4f),

        f32_eq(0x5b),
        f32_ne(0x5c),
        f32_lt(0x5d),
        f32_gt(0x5e),
        f32_le(0x5f),
        f32_ge(0x60),

        i32_add(0x6A),
        i32_sub(0x6B),
        i32_mul(0x6C),
        i32_div_s(0x6D),
        i32_div_u(0x6E),
        i32_rem_s(0x6F),
        i32_rem_u(0x70),

        i32_and(0x71),
        i32_or(0x72),
        i32_xor(0x73),
        i32_shl(0x74),
        i32_shr_s(0x75),
        i32_shr_u(0x76),

        f32_neg(0x8C),
        f32_sqrt(0x91),
        f32_add(0x92),
        f32_sub(0x93),
        f32_mul(0x94),
        f32_div(0x95),

        i32_trunc_f32_s(0xa8),
        f32_convert_i32_s(0xb2);

        public final byte enc;
        opcodes(int x){
            enc = (byte) x;
        }
    }

    enum exportType {
        func(0x00),
        table(0x01),
        mem(0x02),
        global(0x03);

        public final byte enc;
        exportType(int x){
            enc = (byte) x;
        }
    }

    static final byte functionType = 0x60;
    static final byte emptyArray = 0x0;

    static final byte[] header = {0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00};

    static byte[] encodeVector(byte[]... data) {
        int dataLen = data.length;
        byte[] dataLenEncoded = {0x00};
        int resLen = dataLenEncoded.length;
        for(byte[] i : data) resLen += i.length;
        byte[] res = Arrays.copyOf(dataLenEncoded, resLen);

        int offset = dataLenEncoded.length;
        for(byte[] i : data){
            System.arraycopy(i, 0, res, offset, i.length);
            offset += i.length;
        }

        return res;
    }

    static byte[] encodeLocal(int count, valueType type){

    }
}
