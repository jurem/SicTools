. Run with rand.jar addon.
. You can play with the program:
. Stop the execution. If SW is equal to 0x000201, set it to 1 and resume execution.
. This will disable interrupts and only As will be printed. Then stop it again
. and reset SW back to 0x201. Interrupts will be enabled again.

ints	START 0
. Register work area for timer interrupts
	ORG 0x160
	. first byte will be filled by the interrupt's value
	. second nibble of the second byte is interrupt mask (0 = disable all interrupts)
	. the third byte goes to supervisor mode
timer_int	WORD 0x000001 . SW will be set to this after the interrupt has been triggered.
timer_haddr	RESW 1 . The address of handler routine.
	RESW 10 . 9 registers, one of them (F) 2 WORDS long

timer_handler
	. Write "\nTIM\n"
	LDCH #0x0a
	WD #1
	LDCH #0x54
	WD #1
	LDCH #0x49
	WD #1
	LDCH #0x4d
	WD #1
	LDCH #0x0a
	WD #1
	. Get a random number and set the timer to it.
	CLEAR A
	RD #3
	STA rand
	STI rand
	. Return to the program.
	LPS #timer_int
rand	RESW 1


init_regs	RESW 2
	. software will set the SW to the following value:
	WORD 0x000201 . enable TIMER interrupts, supervisor mode (we need WD)
init_addr	RESW 1 . The start address of the program.
	RESW 8 . init all registers to 0


	. Initialization:
entry	LDA #timer_handler
	STA timer_haddr . set the value of timer_haddr to #timer_handler.
	LDA #main
	STA init_addr . set the value of init_addr to #main.
	LPS #init_regs

. main function: loop that prints the character A.
main	LDCH #0x41
	WD #1
	J main

	END entry
