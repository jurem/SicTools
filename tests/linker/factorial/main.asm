main START 0
	EXTREF result
	EXTREF fact
	EXTREF print
	EXTREF stinit
	EXTREF end

	+LDA #end
	+JSUB stinit   . initialize the stack
loop LDA #1
	+STA result     . clear the result
	LDA i
	ADD #1         . i++
	STA i
	COMP #10
	JEQ halt       . if i == 10 then halt
	+JSUB fact
	+LDA result
	+JSUB print    . print the contents of A to stdout
	J loop
	
halt J halt

i WORD 0
gap RESW 64
