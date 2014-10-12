. Tests: START, END
.
. START must be the FIRST
.		FIX
x2		EQU		x1-1
x		EQU 	1
. START expression supports only absolute expressions
struct	START	0xFF + x2
a		FIX
x1		EQU		x+1
. multiple STARTs are now allowed
.		START	0x100
b		FIX
c		FIX
d		FIX
. first=b+1=c
end		END		b + 0x1
