.## 4096 16
loadch	START	0
.	data
aval	WORD	0xffffff
.	prog
main	LDA	aval
	+LDCH	#0x61
	+STCH	res
	LDX	#1
	+LDCH	#0x62
	+STCH	res, X
	LDX	#2
	+LDCH	#0x63
	+STCH	res, X
halt	J	halt
.	data
	ORG	0x1000
res	RESB	3
	END	main
