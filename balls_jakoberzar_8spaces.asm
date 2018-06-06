... -------------------------------
... |         B A L L S           |
... -------------------------------
... This file is indented using 8 spaces.
... Author: Jakob Erzar
... Recommended running frequency: 2000
... Known issues:
...   - One frame for a ball might go wrong while it's going
...     directly through the corner.
start   START   0
        JSUB    skinit  . Init the stack
        ... initialize variables
        LDA     row
        SUB     #1
        STA     rowLmt  . Row limit
        LDA     column
        SUB     #1
        STA     colLmt  . Column limit
        LDT     #3      . Incrementor
        LDA     nBalls
        MUL     #3
        RMO     A, S    . Table limit

        ... draw initial state
        LDX     #0
initlp  JSUB    calcPlc
        LDCH    ballC
        STCH    @ballPlc
        ... increase index
        ADDR    T, X
        COMPR   X, S
        JLT     initlp

lpstart LDX     #0
loop    JSUB    calcPlc
        ... save current place as old
        LDA     ballPlc
        STA     oldPlc

        ... move ball
        JSUB    updtCrd
        JSUB    chckCrd
        JSUB    calcPlc

        ... update screen
        LDCH    emptyC
        STCH    @oldPlc
        LDCH    ballC
        STCH    @ballPlc

        ... loop
        ADDR    T, X
        COMPR   X, S
        JLT     loop
        J       lpstart


... -------------------------------
... |   S U B R O U T I N E S     |
... -------------------------------
        ... calculate ball address
calcPlc LDA     ballY, X
        MUL     column
        ADD     disp
        ADD     ballX, X
        STA     ballPlc
        RSUB

        ... update ball x and y
updtCrd LDA     ballX, X
        ADD     ballVX, X
        STA     ballX, X
        LDA     ballY, X
        ADD     ballVY, X
        STA     ballY, X
        RSUB

        ... check that ball is inside bounds
chckCrd STL     @skptr
        ... push L register
        JSUB    skpush
        ... push S register
        STS     @skptr
        JSUB    skpush
        STS     #0
        ... check X coordinate
        LDA     ballX, X
        COMP    colLmt
        JGT     invertX
        COMP    #0
        JLT     invertX
        ... check Y coordinate
chckVY  LDA     ballY, X
        COMP    rowLmt
        JGT     invertY
        COMP    #0
        JLT     invertY
        ... check if vectors have changed - update position
chckDrt COMPR   S, T
        JEQ     drtyVec
        ... pop S register
chckBck JSUB    skpop
        LDS     @skptr
        ... pop L register
        JSUB    skpop
        LDL     @skptr
        RSUB            ... Return call from chckCrd
... invert X branch
invertX LDA     ballVX, X
        MUL     =-1
        STA     ballVX, X
        RMO     T, S    ... mark as dirty - vectors have changes
        J       chckVY
... invert Y branch
invertY LDA     ballVY, X
        MUL     =-1
        STA     ballVY, X
        RMO     T, S    ... mark as dirty - vectors have changes
        J       chckDrt
... update position for the new vectors branch
drtyVec JSUB    updtCrd	... Move vectors have been changed; revert to old position
        JSUB    updtCrd ... call one more time to move it to new position
        J       chckBck


... -------------------------------
... |         S T A C K           |
... -------------------------------
... Note: Register A is mutated.

... Subroutine Stack Initialize
skinit  LDA     #skstart
        STA     skptr
        RSUB
... Subroutine Stack Push
skpush  LDA     skptr
        ADD     #3
        STA     skptr
        RSUB
... Subroutine Stack Pop
skpop   LDA     skptr
        SUB     #3
        STA     skptr
        RSUB


... -------------------------------
... |        M E M O R Y          |
... -------------------------------
... Number of balls
nBalls  WORD    4
... Ball starting X coord
ballX   WORD    1
        WORD    40
        WORD    0
        WORD    0
... Ball starting Y coord
ballY   WORD    0
        WORD    0
        WORD    12
        WORD    8
... Ball speed (X axis)
ballVX  WORD    1
        WORD    1
        WORD    4
        WORD    2
... Ball speed (Y axis)
ballVY  WORD    1
        WORD    4
        WORD    1
        WORD    1

... Place for storing ball position on screen
oldPlc  RESW    1
ballPlc RESW    1

... Characters for ball and cleaning
ballC   BYTE    X'4F'   . 4F => O
emptyC  BYTE    X'2E'   . 2F => . (for a cool trailing effect)

... Display configuration
disp    WORD    X'00B800'
row     WORD    25
column  WORD    80

... Automatically initialized
rowLmt  RESW    1       . Limit for the rows (Y)
colLmt  RESW    1       . Limit for the columns (X)
... Stack variables
skptr   RESW    1
skstart RESW    200

test    EQU     skptr

        END     start