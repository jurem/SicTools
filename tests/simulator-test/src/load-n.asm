.## 0 32
load	START	0
.	data
y	WORD	3
x	WORD	0x123456
.	prog
main	LDA	@y
	LDX	@y
	LDL	@y
	LDB	@y
	LDS	@y
	LDT	@y
halt	J	halt
	END	main
