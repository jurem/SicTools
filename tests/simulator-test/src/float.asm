.## 128 64
float	START	0
.	prog
main	LDF	=F'1.234567'	. pos + pos = pos
	ADDF	=F'3.456789'
	STF	r1
	LDF	=F'1.234567'	. pos - pos = neg
	SUBF	=F'3.456789'
	STF	r2
	LDF	=F'3.456789'	. pos - pos = pos
	SUBF	=F'1.234567'
	STF	r3
	LDF	r1		. pos * neg = neg
	MULF	r2
	STF	r4
	LDF	r1		. pos * pos = pos
	MULF	r3
	STF	r5
	LDF	r2		. neg * neg = pos
	MULF	r2
	STF	r6
	LDF	r2		. neg / neg = pos
	DIVF	r2
	STF	r7
	LDF	r3		. neg / pos = neg
	DIVF	r2
	STF	r8
	FIX
	FLOAT
	LDA	#0
	LDF	r1
	COMPF	r2
	JGT	j1
	STA	c1
j1	LDF	r3
	COMPF	r1
	JLT	j2
	STA	c2
j2	LDF	r1
	COMPF	r1
	JEQ	halt
	STA	c3
halt	J	halt
.	data
	LTORG
r1	RESW	2
r2	RESW	2
r3	RESW	2
r4	RESW	2
r5	RESW	2
r6	RESW	2
r7	RESW	2
r8	RESW	2
c1	WORD	0xcccccc
c2	WORD	0xcccccc
c3	WORD	0xcccccc
	END	main
