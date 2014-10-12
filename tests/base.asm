. Tests: base-relative, directives BASE, NOBASE

base	START	0xA000

. load B register and notify assembler
		+LDB	#b
        BASE	b

        LDA		#b			base-relative addressing: (B)+0
        LDA		#b			but pc-relative addressing prefered: (PC)+2047
        RESB    2047
b       BYTE    C'FOO'         b displaced by 2048 bytes

. ********** other **********
        LDA		#c			base-relative (since c-b < 4096)
        NOBASE
       +LDA		#c			direct extended, LDA #c would fail here
        RESB    2048
c       BYTE    C'BAR'
