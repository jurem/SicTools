.## 32 32
store	START	0
.	data
aa	WORD	18
ax	WORD	21
al	WORD	24
ab	WORD	27
as	WORD	30
at	WORD	33
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
	STA	@aa
	STX	@ax
	STL	@al
	STB	@ab
	STS	@as
	STT	@at
halt	J	halt
	END	main
