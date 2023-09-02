package jepp.codegen;

import java.util.ArrayList;

public class Encoder {
    private Encoder() {}

    byte[] uLEB128(int x){
        ArrayList<Byte> buffer = new ArrayList<>();
        boolean more = true;
        while (more) {
            byte b = (byte) (x & 0x7f);
            x >>>= 7;
            if ((x == 0 && (b & 0x40) == 0) || (x == -1 && (b & 0x40) != 0)) {
                more = false;
            } else {
                b |= 0x80;
            }
            buffer.add(b);
        }

        byte[] res = new byte[buffer.size()];
        for(int i = 0; i < buffer.size(); i++) res[i] = buffer.get(i);
        return res;
    }

    /*
    export const ieee754 = (n: number) => {
  const buf = Buffer.allocUnsafe(4);
  buf.writeFloatLE(n, 0);
  return Uint8Array.from(buf);
};

export const encodeString = (str: string) => [
  str.length,
  ...str.split("").map(s => s.charCodeAt(0))
];

export const signedLEB128 = (n: number) => {
  const buffer = [];
  let more = true;
  while (more) {
    let byte = n & 0x7f;
    n >>>= 7;
    if ((n === 0 && (byte & 0x40) === 0) || (n === -1 && (byte & 0x40) !== 0)) {
      more = false;
    } else {
      byte |= 0x80;
    }
    buffer.push(byte);
  }
  return buffer;
};

export const unsignedLEB128 = (n: number) => {
  const buffer = [];
  do {
    let byte = n & 0x7f;
    n >>>= 7;
    if (n !== 0) {
      byte |= 0x80;
    }
    buffer.push(byte);
  } while (n !== 0);
  return buffer;
};
     */
}
