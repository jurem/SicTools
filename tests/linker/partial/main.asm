main START 0
	EXTREF m
	EXTREF a
	EXTREF b
	+LDA a
	STA tmp
	+LDA b
	ADD tmp
	STA tmp
	+LDA m
	ADD tmp
	STA tmp
	
tmp RESW 1
	END main
