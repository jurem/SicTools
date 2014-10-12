ext START	0x1000
	EXTREF	push,pop
	EXTREF	a, b, c
	EXTDEF	e

	+LDA		push
	+LDA		pop
	+LDA		a
	+LDA		b
	+LDA		c
six	EQU			2+3
lbl	FIX
e	FLOAT
	END	six
