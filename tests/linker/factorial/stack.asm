stack START 0
	EXTDEF stinit
	EXTDEF push
	EXTDEF pop
	
. starts the stack at the address in A
stinit STA stackptr
	RSUB
	
. pushes the content from A to stack
push STA @stackptr
	LDA stackptr
	ADD #3
	STA stackptr
	RSUB
	
.pops the top element to A
pop LDA stackptr
	SUB #3
	STA stackptr
	LDA @stackptr
	RSUB
	
stackptr RESW 1
	END stack
