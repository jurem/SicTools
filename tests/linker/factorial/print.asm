print START 0
	STA buffer
	
prtbuf LDA buffer
	SUB max        . find first power of 10 larger than buffer
	COMP #0
	JLT found
	LDA max
	MUL #10
	STA max
	J prtbuf
found LDA max          . divide max by 10 and print buffer/max
	DIV #10
	STA max
	COMP #0
	JEQ exit
	LDA buffer
	DIV max
	ADD #48        . ASCII 0
	WD #1
	SUB #48
	MUL max
	STA tmp
	LDA buffer
	SUB tmp
	STA buffer
	J found
exit LDA #1
	STA max        . reset max to 1
	LDA #10        . write newline
	WD #1
	RSUB
max WORD 1
tmp RESW 1
buffer RESW 1
gap RESW 64
