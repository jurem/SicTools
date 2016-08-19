test START 0
	EXTREF extd1
	EXTREF extd2
	LDA #15
	+STA tmp5
	+LDA tmp6
	STA tmp1
	+LDA tmp7
	STA tmp2
	+LDA extd1
	STA tmp3
	+LDA extd2
	+STA tmp8
	RSUB


tmp1 WORD 11
tmp2 WORD 22
tmp3 WORD 33
tmp4 WORD 44
data0 RESB 4096
tmp5 WORD 55
tmp6 WORD 66
tmp7 WORD 77
data1 RESB 4096
tmp8 WORD 88
tmp9 WORD 99
tmp10 WORD 110
