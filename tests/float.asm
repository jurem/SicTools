float	START   0
first	LDF		a
		FIX
		LDA	   #123
		FLOAT
. sum
		LDF     a
		ADDF	b
		STF		sum
. diff
		LDF     a
		SUBF	b
		STF		diff
. mul
		LDF     a
		MULF	b
		STF		prod
. div
		LDF     a
		DIVF	b
		STF		quot
. comp
		LDF     a
		COMPF	b

halt	J		halt

a		FLOT    2.1
b		FLOT	1.2
sum		RESF	1
diff	RESF	1
prod	RESF	1
quot	RESF	1
        END     first

