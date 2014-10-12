. Test: ORG
		LDA		1
		ORG		0x100
		LDA		2
		ORG		0x200
		LDA		3
.
a		EQU		0x1000
		ORG		a
tab		RESB	0x1000
		ORG		tab
x		RESB	1
y		RESB	1
		ORG		tab+0x100
		FIX
