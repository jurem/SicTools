fact START 0
	EXTREF push
	EXTREF pop
	EXTDEF result
	
	COMP #1
	JEQ exit       . if A == 1 then exit
	
	STA tmpA       . store A to tmpA
	STL tmpL       . store L to tmpL
	
	+JSUB push     . push A
	LDA tmpL
	+JSUB push     . push L
	LDA tmpA       . restore A from tmpA
	
	SUB #1         . A--
	JSUB fact      . recursion
	
	+JSUB pop      . pop L
	STA tmpL       . and save it to tmpL
	+JSUB pop      . pop A
	
	MUL result
	STA result     . result = result * A
	LDL tmpL       . restore L
	RSUB
exit RSUB
	
result WORD 1
tmpA RESW 1
tmpL RESW 1
gap RESW 64

