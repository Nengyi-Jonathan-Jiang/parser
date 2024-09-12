# Lexer file format (.lx)

The `LexerReader` class takes in a file of this format and outputs a lexer (lexical analyzer).

### Syntax:

A lexer file is composed of multiple statements that declare rules, which are used by the lexer to group input
characters into tokens.

To declare a rule to match a token, use the syntax `<symbol> := <regex>`, where `<symbol>` is the name of the token and
`<regex>` is a regular expression. For example, to match an identifier (name) in most programming languages, one could
write

```
word := [A-Za-z_]\w*
```

To declare a rule to ignore certain patterns, use the syntax `ignore <regex>`. For example, to ignore C-style
single-line comments, one could write

```
ignore //[^\n]*
```

There is a shorthand to declare rules that match tokens; if a line contains just a sequences of characters without
spaces, it will be matched literally. For example,

```
while
++
```

is equivalent to

```
while := while
++ := \+\+
```

### Regex Features

The regex engine used supports basic syntax including alternation `|`, grouping `()`, Kleene star `*`, Kleene plus `+`,
optional `?`, wildcard `.`, basic character classes `[abc]`/`[^abc]`, and some shorthands such as `\w`, `\d` and `\s`.