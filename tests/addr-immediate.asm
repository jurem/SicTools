. don't run, examine generate code
. immediate addressing
imm		START	0
.imm		START	0x1000		. label first cannot be resolved
.
. ******* numbers *********
. absolute/direct
first	LDA		#0xAB
		LDA		#-1
		LDA		#-2048			min displacement
        LDA		#2047			max displacement
. extended format, absolute
       +LDA		#0x012345
	   +LDA		#0				min address
       +LDA		#1048575		max address

. ********** symbols **********
. absolute
five	EQU		5
        LDA		#five		if absolute symbol then absolute addressing
. pc-relative
		LDA		#a			pc-relative: (PC)+0
a       LDA		#a			pc-relative: (PC)-3
. base-relative
		+LDB	#b
        BASE	b
        LDA		#b			base-relative: (B)+0
        LDA		#b			but pc-relative prefered: (PC)+2047
        RESB    2047
b       BYTE    C'FOO'         b displaced by 2048 bytes

. careful: start address may be too large
		LDA		#first		direct: pc-rel fail, base-rel fail

