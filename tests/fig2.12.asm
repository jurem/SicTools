. changed: device number 05 to 01
COPY    START   0
FIRST   STL     RETADR
CLOOP   JSUB    RDREC
        LDA     LENGTH
        COMP   #0
        JEQ     ENDFIL
        JSUB    WRREC
        J       CLOOP
ENDFIL  LDA    =C'EOF'
        STA     BUFFER
        LDA    #3
        STA     LENGTH
        JSUB    WRREC
        J      @RETADR
		USE		CDATA
RETADR  RESW    1
LENGTH  RESW    1
		USE		CBLKS
BUFFER  RESB    4096
BUFFEND EQU     *
MAXLEN  EQU     BUFFEND-BUFFER
.
.       Subroutine to read record into buffer
.
		USE
RDREC   CLEAR   X
        CLEAR   A
        CLEAR   S
       +LDT    #MAXLEN
RLOOP   TD      INPUT
        JEQ     RLOOP
        RD      INPUT
        COMPR   A,S
        JEQ     EXIT
        STCH    BUFFER,X
        TIXR    T
        JLT     RLOOP
EXIT    STX     LENGTH
        RSUB
		USE		CDATA
INPUT	BYTE	X'F1'
.
.       Subroutine to write record from buffer
.
		USE
WRREC   CLEAR   X
        LDT     LENGTH
WLOOP   TD     =X'01'
        JEQ     WLOOP
        LDCH    BUFFER,X
        WD     =X'01'
        TIXR    T
        JLT     WLOOP
        RSUB
		USE		CDATA
		LTORG
        END     FIRST
