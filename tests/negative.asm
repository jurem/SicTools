test	START	0
main	LDA	#0
	SUB	#1
	COMP	#0	. Into CC returns < (SW = 000040)
	LDA		#-1
	COMP	#0	. Into CC returns > (SW = 000080)
halt	J	halt
	END	main

