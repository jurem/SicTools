.## 4096 16
loadch	START	0
.	data
aval	WORD	0xffffff
.	prog
main	LDA	aval
	+LDCH	string
	+STCH	res
	LDX	#1
	+LDCH	string, X
	+STCH	res, X
	LDX	#2
	+LDCH	string, X
	+STCH	res, X
halt	J	halt
.	data
	ORG	0x1000
string	BYTE	C'STR'
res	RESB	3
	END	main
