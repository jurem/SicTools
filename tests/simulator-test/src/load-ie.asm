.## 0 32
load	START	0
.	prog
main	+LDA	#0xabcde
	+LDX	#0xabcde
	+LDL	#0xabcde
	+LDB	#0x76543
	+LDS	#0x76543
	+LDT	#0x76543
halt	J	halt
	END	main
