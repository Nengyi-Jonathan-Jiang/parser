# JASM/JEPP

A parser and interpreter for two custom languages: a low-level language (Jasm)
and a high-level language (Jepp), implemented in Java, with a focus on LR(1)
parsing and finite-state-machine-based lexing.

Focused on the front-end of a language implementation pipeline.

The lexer was later used to prototype syntax for
the [Cerium](https://github.com/Nengyi-Jonathan-Jiang/cerium/commits/master/)
project.

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
    if(n <= 2) return 1;
    return fib(n - 1) + fib(n - 2);
}

println fib(input int);
```

## Architecture

Both Jasm and Jepp follow a standard language pipeline:

```text
Source code → Lexer → Tokens → LR(1) Parser → AST → Interpreter → Execution
```

## Design Decisions

- **Parsing algorithm**  
  Chose LR(1) parsing for its ability to handle all deterministic context-free
  languages while enabling parse tables to be precomputed. This provides broader
  language support than LL(k) and avoids the manual effort of recursive descent,
  while remaining more efficient than general algorithms like Earley parsing.

- **Custom regex engine**  
  Implemented a regex engine to control worst-case performance. Since
  tokenization requires only a restricted subset of regex features, the engine
  avoids backtracking and guarantees linear-time matching at the cost of reduced
  expressiveness.

- **Online parsing**  
  The parser is designed as an incremental (online) algorithm, consuming tokens
  as they are produced rather than requiring the full input stream. This enables
  bidirectional interaction between the parser and lexer: parsing state can
  influence tokenization decisions, allowing context-sensitive lexing (e.g.,
  resolving identifiers based on prior declarations, as in the lexer
  hack).

- **Language design (Jepp)**  
  Uses dynamic typing with method dispatch based on the runtime types of
  parameters, enabling both method and operator overloading while maintaining a
  simple type system

## Challenges

- Profiled the parser generator using Java Flight Recorder to identify
  bottlenecks, introducing aggressive memoization and finding that binary trees
  outperformed hash tables for this access pattern
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
