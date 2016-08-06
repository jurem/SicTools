func START 0
	EXTREF ref1
	EXTREF ref2
	
	+LDA ref1
	ADD #5
	+STA ref1
	+JSUB ref2
	RSUB
ref2 CSECT
	EXTDEF ref1
	EXTREF data1
	EXTREF data2
	EXTREF data3
	
	+LDA data1
	ADD #5
	+STA data1
	+LDA data2
	ADD #3
	+STA data2
	STA ref1
	RSUB
	
ref1 WORD 1

neki CSECT
	ADD 0
	ADD 0
	RSUB

data CSECT
	EXTDEF data1
	EXTDEF data2
	
data1 RESW 1
data2 RESW 1
	END func
