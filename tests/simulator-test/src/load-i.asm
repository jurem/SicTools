.## 0 32
load	START	0
.	prog
main	LDA	#0xabc
	LDX	#0xabc
	LDL	#0xabc
	LDB	#0x765
	LDS	#0x765
	LDT	#0x765
halt	J	halt
	END	main
