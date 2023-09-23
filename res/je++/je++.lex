COMMENT := (//[^\n]*|/\*([^*]|\*[^/])*?\*?\*/)
STRING-LITERAL := "[^"\\]*(\\.[^"\\]*)*"
INT-LITERAL := (0|-?[123456789][\d]*)
FLOAT-LITERAL := (0|-?[123456789][\d]*)(.[\d]+)?
BOOL-LITERAL := (true|false)

int
float
void

const

decl
impl
class
func
var

if
else
elif
do
while
for
break
continue

return

goto

public
private
protected

static

println
print

input
inputln

read

dealloc
alloc

as

inline

ref
mut

[
]
(
)
{
}

;
,

.

~
~~
!
!!
++
--

=

@
+
+=
-
-=
%
%=
*
*=
/
/=
^
^=
^^
^^=
&
&=
&&
&&=
|
|=
||
||=

>
>=
<
<=
>>
>>=
<<
<<=
!=
==

::

?
:

->

IDENTIFIER := [a-zA-Z_]\w*