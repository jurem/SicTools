.## 983040 32
load	START	0
.	prog
main	LDX	#0
	+LDA	x, X
	LDX	#3
	+LDL	x, X
	LDX	#6
	+LDB	x, X
	LDX	#9
	+LDS	x, X
	LDX	#12
	+LDT	x, X
halt	J	halt
.	data
	ORG	0xf0000
x	WORD	0x012345
	WORD	0x6789ab
	WORD	0xcdef01
	WORD	0x234567
	WORD	0x89abcd
	END	main
