# Cerium (Assembly language)

(Neodymium, the IR; Dysprosium, the language)

### Locations and operations

Cerium is a register-based assembly language. The CeVM (Cerium Virtual Machine) has 8 general-purpose registers labeled
`r0` through `r7` as well as a dedicated stack pointer register `sp`

Operations in Cerium can read and write directly to registers (direct location) or memory locations pointed to by 
registers (indirect location), and they can also have literals as sources. Unary operations are written as

```
<target> <- <op> <s>
```

and binary operations are written as

```
<target> <- <s1> <op> <s2>
```

where `<target>` is the target location of the operation, `<op>` is the unary or binary operation, and `<s>` are the
source locations.

Note that the register used (directly or pointing to) as the source and target locations may overlap, and that
operations are polymorphic -- most can be applied to different combinations of types in their sources and target,
although some combinations of types are not allowed for some operations.

To use a register directly as a location, one writes `[<type> <register>]`, where `<type>` is integer `i`, float `f`,
boolean `b`, char `c`, or rarely, location `l`. For example, negating a float in the `r0` register might look like

```
[f r0] <- - [f r0]
```

and adding the contents of `r0` and `r1` as integers and storing the result in `r2` might look like

```
[i r2] <- [i r0] + [i r1]
```

To use the memory location pointed to by a register as a location, one adds an `@` before the register. For example, to
use the memory location pointed to by `r3` as a char, one can write `[c @ r3]`.

### Arithmetic operations

Cerium supports the following arithmetic operations: addition `+`, subtraction `-`, multiplication `*`, division `/`,
and modulo `%`, and unary negation `-`. All six can accept any combination of ints and floats as their sources and
targets, with automatic conversion. In addition, `c <- c + i` and `i <- c - c` are allowed.

### Bitwise / Logical operations

Cerium supports the following bitwise operations: bitwise or `|`, bitwise and `&`, bitwise xor `^`, and unary bitwise
negation `~`. Bitwise operations can be applied to ints, chars, and booleans, when the source(s) and target are of the
same type.

Cerium also supports left and right shift `<<` and `>>` on ints, with int sources and targets.

### Relational operations

Cerium supports the six relational operations `<`, `>`, `<=`, `>=`, `==`, and `!=`. Cerium supports relational
operations between combinations of ints and floats and between chars.

### Mov and Memcpy

Cerium has a mov operation written as `<target> <- <source>` which performs automatic conversion between types, except
for float to char. Cerium also has a builtin `memcpy` operation with the syntax `memcpy <source> : <len> -> <target>`
where `<len>` must be an integer and `source` and `target` must be indirect locations of type `l`.

### Display and print

### Memory allocation

I'm too lazy to implement a memory allocator in Cerium, so I built it into the VM (think Java Bytecode, but no garbage 
collection). To allocate memory, write `<target> <- alloc <len>` where `<len>` is an integer and `<target>` is a direct 
location of type `l`. To deallocate memory, write `dealloc <target>` 



print_statement := print string

mov_statement := mov any location
cpy_statement := cpy int int int

partial_statement := mov_statement
partial_statement := cpy_statement

// Arithmetic operations

arithmetic_op := add
arithmetic_op := sub
arithmetic_op := mul
arithmetic_op := div
arithmetic_op := mod

arithmetic_statement := arithmetic_op int int int_location
arithmetic_statement := arithmetic_op int float float_location
arithmetic_statement := arithmetic_op float int float_location
arithmetic_statement := arithmetic_op float float float_location

partial_statement := arithmetic_statement

// Bitwise operations

bitwise_op := or
bitwise_op := xor
bitwise_op := and

bitwise_statement := bitwise_op int int int_location
bitwise_statement := bitwise_op bool bool bool_location

partial_statement := bitwise_statement

// Bit Shift operations

sop := shl
sop := shr

shift_statement := sop int int int_location

partial_statement := shift_statement

// JMP and CMP

condition := ucd
condition := eqz
condition := nez
condition := gtz
condition := ltz
condition := gez
condition := lez

jmp_statement := jmp any int condition
cmp_statement := cmp any bool_location condition

partial_statement := jmp_statement
partial_statement := cmp_statement

// DSP and INP

display_statement := dsp any
input_statement := inp location
partial_statement := display_statement
partial_statement := input_statement

// Memory management instructions

alloc_statement := new int int_location
dealloc_statement := del int

partial_statement := alloc_statement
partial_statement := dealloc_statement

partial_statement := dump