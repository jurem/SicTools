main    START   0
first	LDA		=WORD 0x123456
		LDCH	=X'AABBCC'
		WD		=X'41'
		RD		=X'42'
		RD		=X'42'
		RD		=X'42'
		RD		=X'42'
halt	J		halt
        END     first
