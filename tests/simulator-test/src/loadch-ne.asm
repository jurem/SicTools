.## 4096 16
loadch	START	0
.	data
aval	WORD	0xffffff
.	prog
main	LDA	aval
	+LDCH	@saddr
	+STCH	@raddr
halt	J	halt
.	data
	ORG	0x1000
saddr	WORD	6
raddr	WORD	9
string	BYTE	C'STR'
res	RESB	3
	END	main
