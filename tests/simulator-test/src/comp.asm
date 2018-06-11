.## 0 48
comp	START	0
.	data
addr	WORD	3
val	WORD	3
nval	WORD	0xffffff
r1	WORD	0xcccccc
r2	WORD	0xcccccc
r3	WORD	0xcccccc
r4	WORD	0xcccccc
r5	WORD	0xcccccc
r6	WORD	0xcccccc
r7	WORD	0xcccccc
r8	WORD	0xcccccc
.	prog
main	LDA	#3
	COMP	#3
	JEQ	j1
	STA	r1
j1	LDA	#3
	COMP	val
	JEQ	j2
	STA	r2
j2	LDA	#3
	COMP	@addr
	JEQ	j3
	STA	r3
j3	LDA	#2
	COMP	#3
	JLT	j4
	STA	r4
j4	LDA	#2
	COMP	val
	JLT	j5
	STA	r5
j5	LDA	#2
	COMP	@addr
	JLT	j6
	STA	r6
j6	LDA	#2
	COMP	#0xfff
	JGT	j7
	STA	r7
j7	LDA	#2
	COMP	nval
	JGT	halt
	STA	r8
halt	J	halt
	END	main
