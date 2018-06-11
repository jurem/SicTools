.## 0 64
add	START	0
.	data
ps	WORD	0xfff
pl	WORD	0x7fff00
z	WORD	0
ns	WORD	0xffff00
nl	WORD	0x800000
r1	RESW	1
r2	RESW	1
r3	RESW	1
r4	RESW	1
r5	RESW	1
r6	RESW	1
.	prog
main	LDA	ps	. small + small = pos
	ADD	ps
	STA	r1
	LDA	ps	. small + large = overflow
	ADD	pl
	STA	r2
	LDA	pl	. large - small = pos
	ADD	ns
	STA	r3
	LDA	ps	. small - large = neg
	ADD	nl
	STA	r4
	LDA	ns	. - small - small = neg
	ADD	ns
	STA	r5
	LDA	ns	. - small - large = underflow
	ADD	nl
	STA	r6
halt	J	halt
	END	main
