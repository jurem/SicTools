stack   START 0
	EXTDEF stinit
	EXTDEF push
	EXTDEF pop
stinit  STA stackp    . inicializira sklad na naslovu iz A
	RSUB
push    STA @stackp   . spravi vrednost iz A na sklad
	LDA stackp
	ADD #3
	STA stackp
	RSUB
pop     LDA stackp    . spravi vrednost s sklada v A
	SUB #3
	STA stackp
	LDA @stackp
	RSUB
	
stackp RESW 1      . kazalec na sklad

