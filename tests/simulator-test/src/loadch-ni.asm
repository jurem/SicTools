.## 0 16
loadch	START	0
.	data
aval	WORD	0xffffff
string	BYTE	C'STR'
res	RESB	3
.	prog
main	LDA	aval
	LDCH	string
	STCH	res
	LDX	#1
	LDCH	string, X
	STCH	res, X
	LDX	#2
	LDCH	string, X
	STCH	res, X
halt	J	halt
	END	main
