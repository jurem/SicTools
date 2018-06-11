.## 0 32
or	START	0
.	data
p	WORD	0xFEDCBA
n	WORD	0x876543
r1	RESW	1
r2	RESW	1
.	prog
main	LDA	p
	OR	n
	STA	r1
	LDA	n
	OR	p
	STA	r2
halt	J	halt
	END	main
