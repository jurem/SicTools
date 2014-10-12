    ADDR    A,X
    SUBR    L,B
    MULR    S,T
    DIVR    F,A
    COMPR   X,L
. SHIFT 1 .. 16
	.SHIFTL	B,0
	SHIFTL  X,1
	SHIFTL	X,5
	SHIFTR	S,10
    SHIFTR  T,16
    CLEAR   S
    TIXR    T
    SVC     13
