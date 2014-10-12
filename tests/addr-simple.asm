. don't run, examine generate code
. immediate addressing
simple		START	0
.simple		START	0x1000		. label first cannot be resolved in SIC/XE
.simple		START	0x8000		. label first: fallback to old SIC
.
. ******* numbers *********
. absolute/direct
first	LDA		0xAB
		LDA		0xAB, X			+ indexed
		LDA		0				min displacement
        LDA		4095			max displacement
. extended format, absolute
		+LDA	0x012345
		+LDA	0				min address
		+LDA	1048575			max address

. ********** symbols **********
. absolute
five	EQU		5
        LDA		five		if absolute symbol then absolute addressing
. pc-relative
		LDA		a			(PC)+0
a       LDA		a			(PC)-3
		LDA		a, X		+ indexed
. base-relative
		+LDB	#b
        BASE	b
        LDA		b			(B)+0
		LDA		b, X		+ indexed
        LDA		b			but pc-relative prefered: (PC)+2047
        RESB    2047
b       BYTE    C'FOO'         b displaced by 2048 bytes

. careful: start address may be too large
		LDA		first		direct: pc-rel fail, base-rel fail
		ORG		0x1000
c		FIX
		RESB	2084
		NOBASE
		LDA		c			fallback to old SIC

