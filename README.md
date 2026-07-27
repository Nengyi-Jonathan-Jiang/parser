# Parser and Lexer Generators

Java implementations of a lexer based on non-backtracking 
[deterministic finite automata](https://en.wikipedia.org/wiki/Deterministic_finite_automaton)
and an [LR(1)](https://en.wikipedia.org/wiki/Canonical_LR_parser) parser.

Focused on the front-end of a language implementation pipeline.

As proofs of concepts using the tools, this repository also includes implementations of two 
custom languages: a low-level language (Jasm) and a high-level language (Jepp).

The lexer was also later used to prototype syntax for
my [Cerium](https://github.com/Nengyi-Jonathan-Jiang/cerium/commits/master/)
language project.

## Goals

* Explore formal parsing techniques (LR(1))
* Explore the implementation of a regex engine using finite state machines
* Build a complete front-end pipeline from source code to execution
* Keep the system modular (lexer, parser, interpreter separated)
* Explore how different kinds of languages (low-level Jasm vs high-level Jepp)
  require different interpreter designs and parsing strategies
* Explore language design and grammar specification

## Examples

### Lexer and grammar

Example lexer and grammar for a simple expression language with variables and 
arithmetic operations:

```
COMMENT := //[^\n]*|/\*([^*]|\*+[^*/])*\**\*/
LITERAL := 0|-?[123456789][\d]*

(
)
+
-
*
/
var
=
print

IDENTIFIER := \w+
```

```
statements              // This is the root node of the AST

statements __EPSILON__  // statements can be empty
__CHAIN__ statements := statements statement 

statement := var IDENTIFIER = expression
statement := IDENTIFIER = expression
statement := print expression

expression := primary-expression
expression := primary-expression operator expression

operator := +
operator := -
operator := *
operator := /

primary-expression := ( expression )
primary-expression := IDENTIFIER
primary-expression := LITERAL
```

### Jepp program

Example program in Jepp (
see [testcases/test/jepp](https://github.com/Nengyi-Jonathan-Jiang/jasm-jepp/tree/master/test-cases/test/jepp)
for more examples):

```
module main;

func fib(int n) -> int {
    if (n <= 2) return 1;
    return fib(n - 1) + fib(n - 2);
}

println fib(input int);
```

## Architecture

Both Jasm and Jepp follow a standard language pipeline:

```text
Source code → Lexer → Tokens → LR(1) Parser → AST → Interpreter → Execution
```

## Usage

To run the proof of concept languages Jasm and Jepp:
1. **Precompute parse tables**: Execute the main methods in `tasks/BuildXYZParser`
2. **Execute programs**:
   - Run the Jepp interpreter using `tasks/RunInterpreter`
   - Assemble and execute Jasm programs using `tasks/RunAssembler`

The source code for Jasm and Jepp is located in `src/jepp`.

Representative examples of code using the tools include 
`tasks/BuildJasmParser.java`, `src/jepp/frontend/JePPFrontend.java`, 
`src/jepp/interpreter/Interpreter.java`, and `src/jepp/interpreter/JeppInterpreter.java`.
Source code for the parser and lexer generators is contained in `src/frontend` and 
`src/util`. 

## Design Decisions

- **Parsing algorithm**  
  Chose LR(1) parsing for its ability to handle all deterministic context-free
  languages while enabling parse tables to be precomputed. This provides broader
  language support than LL(k) and avoids the manual effort of recursive descent,
  while remaining more efficient than general algorithms like Earley parsing

- **Custom regex engine**  
  Implemented a regex engine to control worst-case performance. Since
  tokenization requires only a restricted subset of regex features, the engine
  avoids backtracking and guarantees linear-time matching at the cost of reduced
  expressiveness

- **Online parsing**  
  The parser is designed as an incremental (online) algorithm, consuming tokens
  as they are produced rather than requiring the full input stream. This enables
  bidirectional interaction between the parser and lexer: parsing state can
  influence tokenization decisions, allowing context-sensitive lexing (e.g.,
  resolving identifiers based on prior declarations, as in the [lexer
  hack](https://en.wikipedia.org/wiki/Lexer_hack))

- **Language design (Jepp)**  
  Uses dynamic typing with runtime parameter-based method dispatch, enabling method
  and operator overloading while keeping the type system simple

## Challenges

- Profiled the parser generator using Java Flight Recorder to isolate bottlenecks;
  introduced aggressive memoization and discovered that binary trees outperformed hash
  tables for the memoized state lookups encountered when processing typical grammars
- Designed an AST simplification pass to reduce tree depth and normalize
  structure, improving traversal and evaluation performance
- Implemented scoped variable and method lookup in the interpreter, supporting
  recursion and multiple dispatch

## Limitations and Future Work

### Current limitations

* Minimal error reporting and diagnostics
* No optimization or compilation stage
* Parsing tables are stored in plain-text format, resulting in large file sizes

### Possible improvements

* Improve error messages and debugging tools
* Extend Jasm with additional features such as user-defined types
* Introduce a compilation step from Jepp to Jasm
* Add a binary format for generated parsing tables
* Allow regex-like syntax in grammar specification
