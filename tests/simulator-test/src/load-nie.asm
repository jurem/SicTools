.## 32768 32
load	START	0
.	prog
main	+LDA	x
	+LDX	x
	+LDL	x
	+LDB	x
	+LDS	x
	+LDT	x
halt	J	halt
.	data
	ORG	0x8000
x	WORD	0x123456
	END	main
