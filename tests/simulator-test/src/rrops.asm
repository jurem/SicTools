.## 0 32
rrops	START	0
.	data
r	RESW	4
c1	WORD	0xcccccc
c2	WORD	0xcccccc
c3	WORD	0xcccccc
a	WORD	0x000123
b	WORD	0xfffcba
c	WORD	0x700000
d	WORD	0x800000
.	prog
main	CLEAR	X
	LDL	#3
	LDA	a
	LDB	b
	LDS	c
	LDT	d
	ADDR	S, A
	STA	r, X
	ADDR	L, X
	SUBR	S, A
	STA	r, X
	ADDR	L, X
	MULR	B, A
	STA	r, X
	ADDR	L, X
	DIVR	B, A
	STA	r, X
.
	COMPR	S, T
	JGT	j1
	STA	c1
j1	COMPR	B, B
	JEQ	j2
	STA	c2
j2	COMPR	B, A
	JLT	halt
	STA	c3
halt	J	halt
	END	main
