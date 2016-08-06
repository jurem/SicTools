strt START 0
	EXTREF a
	EXTREF b
	+LDA a
	ADD #5
	+STA a
halt J halt

sect CSECT
	EXTDEF a
	EXTDEF b
	EXTDEF c
	EXTDEF d
a WORD 1
b WORD 1
c WORD 1
d WORD 1
e WORD 1

sect2 CSECT 
a WORD 1
b WORD 1

 END strt
