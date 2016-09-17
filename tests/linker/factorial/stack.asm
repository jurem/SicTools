stack   START 0
	EXTDEF stinit
	EXTDEF push
	EXTDEF pop
stinit  STA stackptr    . inicializira sklad na naslovu iz A
	RSUB
push    STA @stackptr   . spravi vrednost iz A na sklad
	LDA stackptr
	ADD #3
	STA stackptr
	RSUB
pop     LDA stackptr    . spravi vrednost s sklada v A
	SUB #3
	STA stackptr
	LDA @stackptr
	RSUB
	
stackptr RESW 1         . kazalec na sklad

