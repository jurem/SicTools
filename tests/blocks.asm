. Test: USE
.
struct	START 0x100
a		FIX
b		FIX
		USE		block_1
b1_a	FLOAT
b1_b	FLOAT
		USE
c		FIX
d		FIX

		USE		block_2
b2_a	NORM
b2_b	NORM
b2_c	NORM

		END		b
