test	START	0

main	JSUB	shiftr
halt	J		halt
m1		WORD 	0xFFFFFF
m2		WORD 	0xFFFFFE
m3		WORD 	0xFFFFFD
min		WORD	0x800000

add		LDA		#12
		LDB		#3
		ADDR	B, A
		SUBR	A, A
		LDB		m3
		SUBR	B, A
		ADD		m2
		ADD		m1
		ADD		m2
		ADD		m1
		RSUB

mul		LDA		#6
		LDB		#7
		MULR	B, A
		LDA		m2
		LDB		m3
		MULR	B, A
		RSUB

div		LDA		#6
		LDB		m2
		DIVR	B, A
		RSUB

shiftl	LDA		#1
		SHIFTL	A, 3
		LDA		#0xA
		SHIFTL	A, 4
		SHIFTL	A, 4
		SHIFTL	A, 4
		SHIFTL	A, 4
		SHIFTL	A, 4
		SHIFTL	A, 1
		SHIFTL	A, 1
		SHIFTL	A, 1
		SHIFTL	A, 1
		RSUB

shiftr	LDA		#0xAA
		SHIFTR	A, 1
		SHIFTR	A, 2
		SHIFTR	A, 3
		SHIFTR	A, 4
		LDA		min
		SHIFTR	A, 4
		SHIFTR	A, 4
		SHIFTR	A, 1
		SHIFTR	A, 1
		SHIFTR	A, 1
		SHIFTR	A, 1
		RSUB

		END	main

