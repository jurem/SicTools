package sic.sim.vm;

import sic.sim.breakpoints.ReadDataBreakpointException;
import sic.sim.breakpoints.WriteDataBreakpointException;

public class Interrupt {
    public enum IntClass {
        SVC(8),
        PROGRAM(4),
        TIMER(2),
        IO(1);

        public final int value;

        IntClass(int value) {
            this.value = value;
        }
    }

    public enum ProgramIntCode {
        ILLEGAL_INSTRUCTION(0x00),
        PRIVILEGED_INSTRUCTION(0x01),
        ADDR_OUT_OF_RANGE(0x02),
        MEM_PROTECT(0x03),
        OVERFLOW(0x04),
        PAGE_FAULT(0x10),
        SEG_FAULT(0x11),
        SEG_PROTECTION_VIOLATION(0x12),
        SEG_LEN_EXCEEDED(0x13);

        public final int value;

        ProgramIntCode(int value) {
            this.value = value;
        }
    }

    public IntClass intClass;
    private int intCode = 0;

    public Interrupt(IntClass intClass) {
        this.intClass = intClass;
    }

    public Interrupt(IntClass intClass, int intCode) {
        this.intClass = intClass;
        this.intCode = intCode;
    }

    public Interrupt(IntClass intClass, ProgramIntCode intCode) {
        this.intClass = intClass;
        this.intCode = intCode.value;
    }

    public int getWorkArea() {
        switch (intClass) {
            case SVC:
                return 0x100;
            case PROGRAM:
                return 0x130;
            case TIMER:
                return 0x160;
            case IO:
                return 0x190;
        }
        return -1;
    }

    public void trigger(Registers registers, Memory memory)  throws ReadDataBreakpointException, WriteDataBreakpointException {
        int addr = getWorkArea();

        memory.setWordRaw(addr+6, registers.getSW());
        memory.setWordRaw(addr+9, registers.getPC());
        memory.setWordRaw(addr+12, registers.getA());
        memory.setWordRaw(addr+15, registers.getX());
        memory.setWordRaw(addr+18, registers.getL());
        memory.setWordRaw(addr+21, registers.getB());
        memory.setWordRaw(addr+24, registers.getS());
        memory.setWordRaw(addr+27, registers.getT());
        memory.setFloatRaw(addr+30, registers.getF());

        registers.setSW(memory.getWordRaw(addr));
        registers.setPC(memory.getWordRaw(addr+3));
        registers.setIntCode(intCode);
    }
}
