.## 0 16
routin	START	0
.	prog
main	LDL	#0xfff
	JSUB	test
	LDB	#0x12
halt	J	halt
.
test	LDA	#0xabc
	RSUB
	END	main
