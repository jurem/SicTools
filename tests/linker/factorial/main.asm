main    START 0
	EXTREF result
	EXTREF fact
	EXTREF print
	EXTREF stinit
	
	LDA #gap
	+JSUB stinit    . postavi sklad
loop    LDA #1          
	+STA result     . result = 1
	LDA i
	ADD #1          . i++
	STA i
	COMP #10
	JEQ halt        . if i == 10 then halt
	+JSUB fact      . poklici fact(i)
	+LDA result     
	+JSUB print     . izpisi rezultat
	J loop
halt    J halt

i       WORD 0
gap     RESW 64
        END main