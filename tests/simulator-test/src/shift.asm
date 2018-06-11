.## 0 96
shift	START	0
.	data
valp	WORD	0x123456
valn	WORD	0x800000
tpl	RESW	7
tpr	RESW	7
tnl	RESW	7
tnr	RESW	7
.	prog
main	CLEAR	X
	LDL	valp
	LDB	valp
	LDS	valn
	LDT	valn
loop	SHIFTL	L, 4
	STL	tpl, X
	SHIFTR	B, 4
	STB	tpr, X
	SHIFTL	S, 4
	STS	tnl, X
	SHIFTR	T, 4
	STT	tnr, X
	LDA	#3
	ADDR	A, X
	RMO	X, A
	COMP	#21
	JLT	loop
halt	J	halt
	END	main
