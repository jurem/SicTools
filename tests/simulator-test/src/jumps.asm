.## 0 32
jumps	START	0
.	data
v1	WORD	0xaaaaaa
v2	WORD	0xaaaaaa
v3	WORD	0xaaaaaa
v4	WORD	0xaaaaaa
v5	WORD	0xaaaaaa
v6	WORD	0xaaaaaa
v7	WORD	0xaaaaaa
.	prog
main	LDA	#0
	J	j1
	LDA	#0xfff
j1	STA	v1
	LDA	#0
	COMP	#0
	JEQ	j2
	LDA	#0xfff
j2	STA	v2
	LDA	#0
	COMP	#1
	JEQ	j3
	LDA	#0xfff
j3	STA	v3
	LDA	#0
	COMP	#1
	JLT	j4
	LDA	#0xfff
j4	STA	v4
	LDA	#0
	COMP	#0
	JLT	j5
	LDA	#0xfff
j5	STA	v5
	LDA	#0
	COMP	#0xfff
	JGT	j6
	LDA	#0xfff
j6	STA	v6
	LDA	#0
	COMP	#0
	JGT	j7
	LDA	#0xfff
j7	STA	v7
halt	J	halt
	END	main
