prog    START 0         . prva sekcija z imenom prog
	EXTREF ref1     . reference na tri zunanje simbole
	EXTREF ref2
	EXTREF sect3    . ime sekcije je privzeto zunanji simbol
	+LDA ref1       . naložimo vrednost iz reference ref1
	+STA ref2       . sharnimo jo na naslov iz reference ref2
	+JSUB sect3     . skočimo na naslov z reference sect3
halt    J halt

sect2   CSECT           . druga sekcija z imenom sect2
	EXTDEF ref1     . definirana dva simbola
	EXTDEF ref2
ref1    WORD 15         . prvi simbol - vrednost 15
ref2    RESW 1          . drugi simbol - rezervirano mesto za 1 besedo

sect3   CSECT           . tretja sekcija z imenom sect3
	LDA #15
	ADD #15         
	STA result      . result = 15 + 15
	RSUB            . vrnemo se v prvo sekcijo
result  RESW 1

	END prog        . konec programa

