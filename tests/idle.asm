idle	START 0
	. this should get executed but it won't because processor is
	. in idle state. PC won't change, nothing will be ouput.
	. You can verify in CPU view that A is 0x41 ('A') and instruction is
	. WD #1.
	. If you set the SW (on line 28 of this file) to 0x201, then the As will be printed.
start	WD #1
	J start

	ORG 0x160
timer_int	WORD 0x000001
timer_haddr	RESW 1
	RESW 10

timer_handler
	. Write "X\n"
	LDCH #0x58
	WD #1
	LDCH #0x0a
	WD #1
	STI time
	LPS #timer_int

time	WORD 40


init_regs	RESW 2
	. software will set the SW to the following value:
	WORD 0x000202 . enable TIMER interrupts, IDLE state, user mode
	WORD 0 . The start address of the program (in that case nothing will be run).
	WORD 0x41
	RESW 8 . init all registers to 0


	. Initialization:
entry	LDA #timer_handler
	STA timer_haddr . set the value of timer_haddr to #timer_handler.
	LPS #init_regs

	END entry
