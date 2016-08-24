fact    START 0
	EXTREF push
	EXTREF pop
	EXTDEF result
	
	COMP #1
	JEQ exit     . if A == 1 then exit
	STA tmpA     . shrani A na tmpA
	STL tmpL     . shrani L na tmpL
	
	+JSUB push   . push A
	LDA tmpL
	+JSUB push   . push L
	LDA tmpA     . A = tmpA
	SUB #1       . A--
	JSUB fact    . rekurzivni klic
	
	+JSUB pop    . pop L
	STA tmpL     . shrani na tmpL
	+JSUB pop    . pop A
	
	MUL result
	STA result   . result = result * A
	LDL tmpL     . obnovi L
	RSUB
exit    RSUB
	
result  WORD 1
tmpA    RESW 1
tmpL    RESW 1
gap     RESW 64

