init	START	0
first	FIX

. characters
	BYTE	C'abcd'
	WORD	C'a'
	FLOT	C'a'

. hex encoding
	BYTE	X'CAFEBABE'
	WORD	X'BEEF'
	FLOT	X'2244'

. numbers
	. BYTE [-128,127]
	BYTE	123
	BYTE	-100
	BYTE	254

	. WORD [...]
	WORD	123
	WORD	-100
	WORD	254

	. FLOT [...]
	FLOT	123
	FLOT	-10
	FLOT	0

. multiple initializers
	BYTE	C'A', X'41'
	BYTE	42, 0b101, 0o10
	WORD	12345, -1
	WORD	0xCCBBAA
	FLOT	1,2

	END		first
