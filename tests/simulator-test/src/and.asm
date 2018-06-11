.## 0 32
and	START	0
.	data
p	WORD	0xFEDCBA
n	WORD	0x876543
r1	RESW	1
r2	RESW	1
.	prog
main	LDA	p
	AND	n
	STA	r1
	LDA	n
	AND	p
	STA	r2
halt	J	halt
	END	main
