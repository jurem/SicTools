main START 0
	EXTREF stinit
	EXTREF push
	EXTREF pop
	
.init the stack at #256
	LDA #256
	+JSUB stinit

.push 0x11 0x22 0x33 0x44 to stack
	LDA #17
	+JSUB push
	LDA #34
	+JSUB push
	LDA #51
	+JSUB push
	LDA #68
	+JSUB push
	
.pop them back and store them to memory again
	+JSUB pop
	STA res1
	+JSUB pop
	STA res2
	+JSUB pop
	STA res3
	+JSUB pop
	STA res4
halt J halt
	
res1 WORD 170
res2 WORD 170
res3 WORD 170
res4 WORD 170
