. Test: CSECT
.
sect	START 0x100
a		FIX
		USE		b1
c		FIX
		USE
b		FIX

read	CSECT
a		FLOAT
		USE		b2
c		FLOAT
		USE
b		FLOAT

write	CSECT
a		NORM
		USE		b3
d		NORM
		USE
b		NORM
		USE		b4
e		NORM
		USE
c		NORM

		END		a
