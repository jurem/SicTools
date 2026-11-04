package sic.sim.vm;

import sic.common.*;
import sic.sim.breakpoints.DataBreakpointException;
import sic.sim.breakpoints.ReadDataBreakpointException;
import sic.sim.breakpoints.WriteDataBreakpointException;

import java.util.Stack;

/**
 * @author jure
 */
public class Machine {
    // ************ Constants
    public static final int MAX_ADDRESS = (1 << 20) - 1; // 1048576 - 1
    public static final int MAX_DEVICE = 255;

    // ************ Machine parts
    public final Registers registers = new Registers();
    public final Memory memory = new Memory(MAX_ADDRESS+1);
    public final Devices devices = new Devices(MAX_DEVICE+1);

    private Interrupt svcInt = null;
    private Interrupt programInt = null;
    private Interrupt timerInt = null;
    private Interrupt ioInt = null;

    private Stack<Integer> callStack = new Stack<>();

    // ************ Statistics
    private int instructionCount = 0;
    private MemorySpan lastExecAddr = new MemorySpan();
    private MemorySpan lastExecRead = new MemorySpan();
    private MemorySpan lastExecWrite = new MemorySpan();

    // ************ getters/setters
    public int getInstructionCount() {
        return instructionCount;
    }

    public MemorySpan getLastExecAddr() {
        return lastExecAddr;
    }

    public MemorySpan getLastExecRead() {
        return lastExecRead;
    }

    private void setLastExecRead(int startAddress, int spanLength) {
        lastExecWrite.clear();
        lastExecRead.set(startAddress, spanLength);
    }

    public MemorySpan getLastExecWrite() {
        return lastExecWrite;
    }

    private void setLastExecWrite(int startAddress, int spanLength) {
        lastExecRead.clear();
        lastExecWrite.set(startAddress, spanLength);
    }

    public void clearLastExecReadWrite() {
        lastExecWrite.clear();
        lastExecRead.clear();
        lastExecAddr.clear();
    }

    /**
     * Get the address on the top of the call stack, so we can step out.
     * @return null if no item on stack - no JSUB encountered, otherwise last address.
     */
    public Integer getReturnAddress() {
        if (callStack.isEmpty()) {
            return null;
        }
        return callStack.peek();
    }

    // ********** Execution *********************
    // ********** Instruction types *********************
    private abstract class Instruction {
        protected int opcode;

        abstract public void execute() throws DataBreakpointException;

        public boolean isPrivileged() {
            return Opcode.isPrivileged(opcode);
        }
    }

    private class InstructionF1 extends Instruction {
        public InstructionF1(int opcode) {
            this.opcode = opcode;
        }

        @Override
        public void execute() throws DataBreakpointException {
            switch (opcode) {
                case Opcode.FLOAT:
                    registers.setF((double) registers.getAs()); break;
                case Opcode.FIX:
                    registers.setA((int) registers.getF()); break;
                case Opcode.NORM:
                    notImplemented("NORM"); break;
                case Opcode.SIO:
                    notImplemented("SIO"); break;
                case Opcode.HIO:
                    notImplemented("HIO"); break;
                case Opcode.TIO:
                    notImplemented("TIO"); break;
            }
        }
    }

    private class InstructionF2 extends Instruction {
        private int operand;

        public InstructionF2(int opcode, int operand) {
            this.opcode = opcode;
            this.operand = operand;
        }

        @Override
        public void execute() throws DataBreakpointException {
            // Format 2: OP o1, o2 - two 4-bit operands
            int o1 = (operand & 0xF0) >> 4;
            int o2 = (operand & 0x0F);
            switch (opcode) {
                case Opcode.ADDR:
                    registers.set(o2, registers.get(o2) + registers.get(o1)); break;
                case Opcode.SUBR:
                    registers.set(o2, registers.get(o2) - registers.get(o1)); break;
                case Opcode.MULR:
                    registers.set(o2, registers.get(o2) * registers.get(o1)); break;
                case Opcode.DIVR:
                        int divisor = registers.get(o1);
                        if (divisor == 0) {
                            divisionByZero();
                        } else {
                            registers.set(o2, registers.gets(o2) / divisor);
                        }
                        break;
                case Opcode.COMPR:
                    registers.setCC(registers.gets(o1) - registers.gets(o2)); break;
                case Opcode.SHIFTL:
                    registers.set(o1, registers.get(o1) << (o2 + 1) | registers.get(o1) >> (24 - o2 - 1)); break;
                case Opcode.SHIFTR:
                    registers.set(o1, registers.gets(o1) >> (o2 + 1)); break;
                case Opcode.RMO:
                    registers.set(o2, registers.get(o1)); break;
                case Opcode.CLEAR:
                    registers.set(o1, 0); break;
                case Opcode.TIXR:
                    registers.setX(registers.getX()+1);
                    registers.setCC(registers.getXs() - registers.gets(o1));
                    break;
                case Opcode.SVC:
                    if (!registers.intEnabled(Interrupt.IntClass.SVC)) {
                        Logger.fmterr("SVC is disabled");
                        break;
                    }
                    svcInt = new Interrupt(Interrupt.IntClass.SVC, operand);
                    break;
            }
        }
    }

    private class InstructionSICF3F4 extends Instruction {
        private Flags flags;
        private int operand;

        public InstructionSICF3F4(int opcode, Flags flags, int operand) {
            this.opcode = opcode;
            this.flags = flags;
            this.operand = operand;
        }

        @Override
        public void execute() throws DataBreakpointException {
            // Formats: SIC, F3, F4
            switch (opcode) {
                // ***** immediate addressing not possible *****
                // stores
                case Opcode.STA:
                    storeWord(flags, operand, registers.getA()); break;
                case Opcode.STX:
                    storeWord(flags, operand, registers.getX()); break;
                case Opcode.STL:
                    storeWord(flags, operand, registers.getL()); break;
                case Opcode.STCH:
                    storeByte(flags, operand, registers.getA()); break;
                case Opcode.STB:
                    storeWord(flags, operand, registers.getB()); break;
                case Opcode.STS:
                    storeWord(flags, operand, registers.getS()); break;
                case Opcode.STF:
                    storeFloat(flags, operand, registers.getF()); break;
                case Opcode.STT:
                    storeWord(flags, operand, registers.getT()); break;
                case Opcode.STSW:
                    storeWord(flags, operand, registers.getSW()); break;

                // jumps
                case Opcode.JEQ:
                    if (registers.ccIsEqual()) {
                        registers.setPC(resolveAddr(flags, operand));
                    };
                    break;
                case Opcode.JGT:
                    if (registers.ccIsGreater()) {
                        registers.setPC(resolveAddr(flags, operand));
                    };
                    break;
                case Opcode.JLT:
                    if (registers.ccIsLower()) {
                        registers.setPC(resolveAddr(flags, operand));
                    };
                    break;
                case Opcode.J:
                    registers.setPC(resolveAddr(flags, operand)); break;
                case Opcode.RSUB:
                    registers.setPC(registers.getL());
                    callStack.pop();
                    break;
                case Opcode.JSUB:
                    registers.setL(registers.getPC());
                    callStack.push(registers.getPC());
                    registers.setPC(resolveAddr(flags, operand));
                    break;
                // ***** immediate addressing possible *****

                // loads
                case Opcode.LDA:
                    registers.setA(loadWord(flags, operand)); break;
                case Opcode.LDX:
                    registers.setX(loadWord(flags, operand)); break;
                case Opcode.LDL:
                    registers.setL(loadWord(flags, operand)); break;
                case Opcode.LDCH:
                    registers.setALo(loadByte(flags, operand)); break;
                case Opcode.LDB:
                    registers.setB(loadWord(flags, operand)); break;
                case Opcode.LDS:
                    registers.setS(loadWord(flags, operand)); break;
                case Opcode.LDF:
                    registers.setF(loadFloat(flags, operand)); break;
                case Opcode.LDT:
                    registers.setT(loadWord(flags, operand)); break;

                // arithmetic
                case Opcode.ADD:
                    registers.setA(registers.getA() + loadWord(flags, operand)); break;
                case Opcode.SUB:
                    registers.setA(registers.getA() - loadWord(flags, operand)); break;
                case Opcode.MUL:
                    registers.setA(registers.getA() * loadWord(flags, operand)); break;
                case Opcode.DIV:
                    int divisor = SICXE.swordToInt(loadWord(flags, operand));
                    if (divisor == 0) {
                        divisionByZero();
                    } else {
                        registers.setA(registers.getAs() / divisor);
                    }
                    break;
                case Opcode.AND:
                    registers.setA(registers.getA() & loadWord(flags, operand)); break;
                case Opcode.OR:
                    registers.setA(registers.getA() | loadWord(flags, operand)); break;
                case Opcode.COMP:
                    registers.setCC(registers.getAs() - SICXE.swordToInt(loadWord(flags, operand))); break;
                case Opcode.TIX:
                    registers.setX(registers.getX() + 1);
                    registers.setCC(registers.getXs() - SICXE.swordToInt(loadWord(flags, operand)));
                    break;

                // input/output
                case Opcode.RD:
                    registers.setALo(devices.read(loadByte(flags, operand)));  break;
                case Opcode.WD:
                    devices.write(loadByte(flags, operand), registers.getALo()); break;
                case Opcode.TD:
                    registers.setCC(devices.test(loadByte(flags, operand)) ? -1 : 0); break;

                // floating point arithmetic
                case Opcode.ADDF:
                    registers.setF(registers.getF() + loadFloat(flags, operand)); break;
                case Opcode.SUBF:
                    registers.setF(registers.getF() - loadFloat(flags, operand)); break;
                case Opcode.MULF:
                    registers.setF(registers.getF() * loadFloat(flags, operand)); break;
                case Opcode.DIVF:
                    registers.setF(registers.getF() / loadFloat(flags, operand)); break;
                case Opcode.COMPF:
                    double sub = registers.getF() - loadFloat(flags, operand);
                    registers.setCC(sub > 0 ? 1 : (sub < 0 ? -1 : 0));
                    break;

                // others
                case Opcode.LPS:
                    lps(operand); break;
                case Opcode.STI:
                    registers.setTimer(loadWord(flags, operand)); break;
                case Opcode.SSK:
                    notImplemented("SSK"); break;
            }
        }

        private int loadWord(Flags flags, int operand) throws ReadDataBreakpointException {
            if (flags.isImmediate()) return operand;
            int addr = resolveAddr(flags, operand);
            setLastExecRead(addr, 3);
            return memory.getWord(addr);
        }

        private int loadByte(Flags flags, int operand) throws ReadDataBreakpointException {
            if (flags.isImmediate()) return operand;
            int addr = resolveAddr(flags, operand);
            setLastExecRead(addr, 1);
            return memory.getByte(addr);
        }

        private double loadFloat(Flags flags, int operand) throws ReadDataBreakpointException {
            if (flags.isImmediate()) return operand;
            int addr = resolveAddr(flags, operand);
            setLastExecRead(addr, 6);
            return memory.getFloat(addr);
        }

        private void storeWord(Flags flags, int operand, int word) throws WriteDataBreakpointException {
            int addr = resolveAddr(flags, operand);
            setLastExecWrite(addr, 3);
            memory.setWord(addr, word);
        }

        private void storeByte(Flags flags, int operand, int _byte) throws WriteDataBreakpointException {
            int addr = resolveAddr(flags, operand);
            setLastExecWrite(addr, 1);
            memory.setByte(addr, _byte);
        }

        private void storeFloat(Flags flags, int operand, double _float) throws WriteDataBreakpointException {
            int addr = resolveAddr(flags, operand);
            setLastExecWrite(addr, 6);
            memory.setFloat(addr, _float);
        }

        // use of TA for store: addr / addr of addr
        private int resolveAddr(Flags flags, int addr) {
            if (flags.isIndirect()) {
                addr = memory.getWordRaw(addr);
                // SicTools extension
                if (flags.isIndexed())
                    addr += registers.getXs();
            }
            return addr;
        }

        private void lps(int addr) throws ReadDataBreakpointException {
            registers.setSW(memory.getWordRaw(addr+6));
            registers.setPC(memory.getWordRaw(addr+9));
            registers.setA(memory.getWordRaw(addr+12));
            registers.setX(memory.getWordRaw(addr+15));
            registers.setL(memory.getWordRaw(addr+18));
            registers.setB(memory.getWordRaw(addr+21));
            registers.setS(memory.getWordRaw(addr+24));
            registers.setT(memory.getWordRaw(addr+27));
            registers.setF(memory.getFloatRaw(addr+30));
        }

    }

    private class InvalidInstruction extends Instruction {
        public InvalidInstruction(int opcode) {
            this.opcode = opcode;
        }

        @Override
        public void execute() throws DataBreakpointException {
            if ( registers.intEnabled(Interrupt.IntClass.PROGRAM)) {
                programInt = new Interrupt(Interrupt.IntClass.PROGRAM,
                        Interrupt.ProgramIntCode.ILLEGAL_INSTRUCTION);
            } else {
                Logger.fmterr("Invalid opcode '%d'.", opcode);
            }
        }

        @Override
        public boolean isPrivileged() {
            return false;
        }
    }

    // ********** Utility functions for fetch and execute *****************
    private void notImplemented(String mnemonic) {
        Logger.fmterr("Instruction '%s' not implemented!", mnemonic);
    }

    private void invalidAddressing() {
        if ( registers.intEnabled(Interrupt.IntClass.PROGRAM)) {
            programInt = new Interrupt(Interrupt.IntClass.PROGRAM,
                    Interrupt.ProgramIntCode.ILLEGAL_INSTRUCTION);
        } else {
            Logger.err("Invalid addressing.");
        }
    }

    private void divisionByZero() {
        if ( registers.intEnabled(Interrupt.IntClass.PROGRAM)) {
            programInt = new Interrupt(Interrupt.IntClass.PROGRAM,
                    Interrupt.ProgramIntCode.ILLEGAL_INSTRUCTION);
        } else {
            Logger.err("division by zero");
        }
    }

    public int fetchByte() {
        int b = memory.getByteRaw(registers.getPC());
        registers.incPC();
        return b;
    }

    public void triggerInterrupts() throws DataBreakpointException {
        // Setting interrupt to null clears it.
        if (svcInt != null) {
            svcInt.trigger(registers, memory);
            svcInt = null;
        } else if (programInt != null) {
            programInt.trigger(registers, memory);
            programInt = null;
        } else if (timerInt != null) {
            timerInt.trigger(registers, memory);
            timerInt = null;
        } else if (ioInt != null) {
            // TODO: IO interrupts.
            // We should probably support multiple simultaneous IO interrupts.
            // Should they also get cleared by the "HW" device?
            ioInt.trigger(registers, memory);
            ioInt = null;
        }
    }

    // Each clock cycle performs two steps:
    // 1. fetch and decode
    // 2. execute
    public Instruction fetchDecode() {
        int instructionSize = 0;
        Instruction instruction = null;
        int opcode = 0;

        instructionCount++;
        clearLastExecReadWrite();

        lastExecAddr.setStartAddress(registers.getPC());
        lastExecAddr.setSpanLength(0);

        // Fetch the first byte.
        opcode = fetchByte();
        if (Opcode.isF1(opcode)) {
            instruction = new InstructionF1(opcode);
            instructionSize = 1;
        } else if (Opcode.isF2(opcode)) {
            int op = fetchByte();
            instruction = new InstructionF2(opcode, op);
            instructionSize = 2;
        } else if (Opcode.isF34(opcode & 0xFC)) {
            int op = fetchByte();
            Flags flags = new Flags(opcode, op);
            int operand;
            if (flags.isSic()) {
                // ****** Standard SIC *******
                instructionSize = 3;
                operand = flags.operandSic(op, fetchByte());
            } else if (flags.isExtended()) {
                // ****** F4 (extended) ******
                instructionSize = 4;
                operand = flags.operandF4(op, fetchByte(), fetchByte());

                if (flags.isRelative()) {
                    invalidAddressing();
                }
            } else {
                // ****** F3 *****************
                instructionSize = 3;
                operand = flags.operandF3(op, fetchByte());

                // Handle relative addressing.
                if (flags.isPCRelative()) {
                    operand = flags.operandPCRelative(operand) + registers.getPC();
                } else if (flags.isBaseRelative()) {
                    operand += registers.getB();
                } else if (!flags.isAbsolute()) {
                    // both PC and base at the same time
                    invalidAddressing();
                }
            }

            // Handle indexed addressing.
            if (flags.isIndexed()) {
                // SIC, F3, F4 -- all support indexed addressing, but only when
                // simple TA calculation used
                // Indirect indexed addressing is an extension of SicTools to
                // simplify complex programs.
                if (flags.isSimple()) {
                    operand += registers.getXs();
                } else if (flags.isIndirect()) {
                    // Method resolveAddr will add X after the first resolution.
                    // I hope empty elif clause is more readable than
                    // negation of this condition for invalid addressing.
                } else {
                    invalidAddressing();
                }
            }
            instruction = new InstructionSICF3F4(opcode & 0xFC, flags, operand);
        } else {
            instruction = new InvalidInstruction(opcode);
        }
        lastExecAddr.setSpanLength(instructionSize);
        return instruction;
    }

    public void step() throws DataBreakpointException {
        if (!registers.isIdle()) {
            Instruction instruction = fetchDecode();
            if (instruction.isPrivileged() && !registers.isSupervisor()) {
                if (registers.intEnabled(Interrupt.IntClass.PROGRAM)) {
                    programInt = new Interrupt(Interrupt.IntClass.PROGRAM,
                            Interrupt.ProgramIntCode.PRIVILEGED_INSTRUCTION);
                } else {
                    Logger.err("skipping execution of privileged instruction");
                }
            } else {
                instruction.execute();
            }
        }
        registers.tickTimer();
        if (registers.getTimer() <= 0 && registers.intEnabled(Interrupt.IntClass.TIMER) && timerInt == null) {
            timerInt = new Interrupt(Interrupt.IntClass.TIMER);
        }
        triggerInterrupts();
    }
}
