.## 0 16
loadch	START	0
.	data
aval	WORD	0xffffff
saddr	WORD	9
raddr	WORD	12
string	BYTE	C'STR'
res	RESB	3
.	prog
main	LDA	aval
	LDCH	@saddr
	STCH	@raddr
halt	J	halt
	END	main
