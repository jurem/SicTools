.## 0 32
store	START	0
.	data
a	RESW	5
.	prog
main	LDA	#0xdef
	LDL	#0xf01
	LDB	#0x012
	LDS	#0x123
	LDT	#0x234
	LDX	#0
	STA	a, X
	LDX	#3
	STL	a, X
	LDX	#6
	STB	a, X
	LDX	#9
	STS	a, X
	LDX	#12
	STT	a, X
halt	J	halt
	END	main
