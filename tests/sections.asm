. Test: CSECT
.
sect	START 0x100
a		FIX
b		FIX
c		FIX

read	CSECT
a		FLOAT
b		FLOAT
c		FLOAT

write	CSECT
c		NORM
b		NORM
a		NORM

.		Sections without labels are not allowed
.		CSECT
.		Reentering sections is not allowed
.read	CSECT

		END		a
