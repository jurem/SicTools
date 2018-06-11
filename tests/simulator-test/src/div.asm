.## 0 32
div	START	0
.	data
ps	WORD	0xfff
pl	WORD	0x7fff00
ns	WORD	0xffff00
nl	WORD	0x800000
r1	RESW	1
r2	RESW	1
r3	RESW	1
r4	RESW	1
.	prog
main	LDA	pl	. large / small
	DIV	ps
	STA	r1
	LDA	ps	. small / large
	DIV	pl
	STA	r2
	LDA	ps	. small / - small
	DIV	ns
	STA	r3
	LDA	nl	. - large / - small
	DIV	ns
	STA	r4
halt	J	halt
	END	main
