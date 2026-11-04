ints	START 0


	ORG 0x100
	. first byte will be filled by the interrupt's value
	. second nibble of the second byte is interrupt mask (0 = disable all interrupts)
	. the third byte goes to supervisor mode
svc_int	WORD 0x000001
svc_haddr	RESW 1
	RESW 10 . 9 registers, one of them 2 WORDS long

svc_handler	LDCH #0x53
	WD #1
	LDCH #0x56
	WD #1
	LDCH #0x43
	WD #1
	LDCH #0x20
	WD #1

	. print code
	LDCH #0x30
	WD #1
	LDCH #0x58
	WD #1

	STSW sw
	CLEAR A
	LDCH sw
	SHIFTR A,4
	AND #0xf
	COMP #0xa
	JLT add
	ADD #0x7
add	ADD #0x30
	WD #1

	LDCH sw
	AND #0xf
	COMP #0xa
	JLT add2
	ADD #0x7
add2	ADD #0x30
	WD #1
	LDCH #0x0a
	WD #1
	. This SVC interrupt will not happen because all interrupts are masked.
	SVC 13
	LPS #svc_int


init_regs RESW 2
	WORD 0x000801 . enable SVC, supervisor mode (because of WD)
init_addr	RESW 1
	RESW 8 . init all registers to 0


sw	RESW 1

	. switch to user mode, enable interrupts
entry	LDA #svc_handler
	STA svc_haddr
	LDA #main
	STA init_addr
	LPS #init_regs

. main function: print A, then request two interrupts and print A (to show that
. the register stays the same) and then B
main	LDCH #0x41
	WD #1
	SVC 0x1a
	SVC 0x2f
	WD #1
	LDCH #0x42
	WD #1
halt	J halt

	END entry
