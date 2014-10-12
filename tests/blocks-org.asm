. Test: USE
.
struct	START 	0x100
first	FIX
		FIX

. first block
		USE		block_1
		FLOAT
		ORG		0x1000
		FLOAT
		FLOAT
		ORG
		FLOAT
		FLOAT

		USE
		FIX
		FIX

. second block
		USE		block_2
		NORM
		NORM
		NORM
		NORM

		END		first
