.## 0 32
load	START	0
.	data
x	WORD	0x123456
.	prog
main	LDA	x
	LDX	x
	LDL	x
	LDB	x
	LDS	x
	LDT	x
halt	J	halt
	END	main
