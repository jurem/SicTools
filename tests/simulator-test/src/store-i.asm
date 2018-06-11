.## 0 32
store	START	0
.	data
a	RESW	1
x	RESW	1
l	RESW	1
b	RESW	1
s	RESW	1
t	RESW	1
.	prog
main	LDA	#0xdef
	LDX	#0xef0
	LDL	#0xf01
	LDB	#0x012
	LDS	#0x123
	LDT	#0x234
	STA	#a
	STX	#x
	STL	#l
	STB	#b
	STS	#s
	STT	#t
halt	J	halt
	END	main
