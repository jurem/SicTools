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

    public static final int MAX_ADDRESS = (1 << 20) - 1; // 1048576 - 1
    public static final int MAX_DEVICE = 255;

    // ************ Machine parts

    public final Registers registers;
    public final Memory memory;
    public final Devices devices;

    // ************ Statistics

    private int instructionCount;
    private MemorySpan lastExecAddr;
    private MemorySpan lastExecRead;
    private MemorySpan lastExecWrite;


    private Stack<Integer> addressBelowJSUB = new Stack<>();

    private boolean indirectX = false;

    // ************ Constructor

    public Machine() {
        this.registers = new Registers();
        this.memory = new Memory(MAX_ADDRESS+1);
        this.devices = new Devices(MAX_DEVICE+1);
        this.lastExecRead = new MemorySpan();
        this.lastExecWrite = new MemorySpan();
        this.lastExecAddr = new MemorySpan();
    }

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

    // ********** Execution *********************

    private void notImplemented(String mnemonic) {
        Logger.fmterr("Instruction '%s' not implemented!", mnemonic);
    }

    private void invalidOpcode(int opcode) {
        Logger.fmterr("Invalid opcode '%d'.", opcode);
    }

    private void invalidAddressing() {
        Logger.err("Invalid addressing.");
    }

    private boolean execF1(int opcode) {
        // Format 1: no operand
        switch (opcode) {
            case Opcode.FLOAT:	registers.setF((double) registers.getAs()); break;
            case Opcode.FIX:	registers.setA((int) registers.getF()); break;
            case Opcode.NORM:	notImplemented("NORM"); break;
            case Opcode.SIO:	notImplemented("SIO"); break;
            case Opcode.HIO:	notImplemented("HIO"); break;
            case Opcode.TIO:	notImplemented("TIO"); break;
            default:			return false;
        }
        return true;
    }

    private boolean execF2(int opcode, int operand) {
        // Format 2: OP o1, o2 - two 4-bit operands
        int o1 = (operand & 0xF0) >> 4;
        int o2 = (operand & 0x0F);
        switch (opcode) {
            case Opcode.ADDR:	registers.set(o2, registers.get(o2) + registers.get(o1)); break;
            case Opcode.SUBR:	registers.set(o2, registers.get(o2) - registers.get(o1)); break;
            case Opcode.MULR:	registers.set(o2, registers.get(o2) * registers.get(o1)); break;
            case Opcode.DIVR:
                    int divisor = registers.get(o1);
                    if (divisor == 0) {
                            System.out.println("division by zero");
                    } else {
                            registers.set(o2, registers.gets(o2) / divisor);
                    }
                    break;
            case Opcode.COMPR:	registers.setSWAfterCompare(registers.gets(o1) - registers.gets(o2)); break;
            case Opcode.SHIFTL:	registers.set(o1, registers.get(o1) << (o2 + 1) | registers.get(o1) >> (24 - o2 - 1)); break;
            case Opcode.SHIFTR:	registers.set(o1, registers.gets(o1) >> (o2 + 1)); break;
            case Opcode.RMO:	registers.set(o2, registers.get(o1)); break;
            case Opcode.CLEAR:	registers.set(o1, 0); break;
            case Opcode.TIXR:	registers.setX(registers.getX()+1);
                                registers.setSWAfterCompare(registers.getXs() - registers.gets(o1));
                break;
            case Opcode.SVC:	notImplemented("SVC"); break;
            default: return false;
        }
        return true;
    }

    // load

    
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

    // use of TA for store: addr / addr of addr
    private int resolveAddr(Flags flags, int addr) {
        if (flags.isIndirect()) {
			addr = memory.getWordRaw(addr);
			if (indirectX)
				addr += registers.getXs();
		}
		return addr;
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

    private boolean execSICF3F4(int opcode, Flags flags, int operand) throws DataBreakpointException {
        // Formats: SIC, F3, F4
        switch (opcode) {
            // ***** immediate addressing not possible *****
            // stores
            case Opcode.STA:	storeWord(flags, operand, registers.getA()); break;
            case Opcode.STX:	storeWord(flags, operand, registers.getX()); break;
            case Opcode.STL:	storeWord(flags, operand, registers.getL()); break;
            case Opcode.STCH:	storeByte(flags, operand, registers.getA()); break;
            case Opcode.STB:	storeWord(flags, operand, registers.getB()); break;
            case Opcode.STS:	storeWord(flags, operand, registers.getS());	break;
            case Opcode.STF:	storeFloat(flags, operand, registers.getF()); break;
            case Opcode.STT:	storeWord(flags, operand, registers.getT()); break;
            case Opcode.STSW:	storeWord(flags, operand, registers.getSW()); break;
            // jumps
            case Opcode.JEQ:	if (registers.isEqual()) registers.setPC(resolveAddr(flags, operand)); break;
            case Opcode.JGT:	if (registers.isGreater()) registers.setPC(resolveAddr(flags, operand)); break;
            case Opcode.JLT:	if (registers.isLower()) registers.setPC(resolveAddr(flags, operand)); break;
            case Opcode.J:		registers.setPC(resolveAddr(flags, operand)); break;
            case Opcode.RSUB:	registers.setPC(registers.getL()); popJSUB(); break;
            case Opcode.JSUB:	registers.setL(registers.getPC()); pushJSUB(); registers.setPC(resolveAddr(flags, operand)); break;
            // ***** immediate addressing possible *****
            // loads
            case Opcode.LDA:	registers.setA(loadWord(flags, operand)); break;
            case Opcode.LDX:	registers.setX(loadWord(flags, operand)); break;
            case Opcode.LDL:	registers.setL(loadWord(flags, operand)); break;
            case Opcode.LDCH:	registers.setALo(loadByte(flags, operand)); break;
            case Opcode.LDB:	registers.setB(loadWord(flags, operand)); break;
            case Opcode.LDS:	registers.setS(loadWord(flags, operand)); break;
            case Opcode.LDF:	registers.setF(loadFloat(flags, operand)); break;
            case Opcode.LDT:	registers.setT(loadWord(flags, operand)); break;
            // arithmetic
            case Opcode.ADD:	registers.setA(registers.getA() + loadWord(flags, operand)); break;
            case Opcode.SUB:	registers.setA(registers.getA() - loadWord(flags, operand)); break;
            case Opcode.MUL:	registers.setA(registers.getA() * loadWord(flags, operand)); break;
            case Opcode.DIV:
                    int divisor = SICXE.swordToInt(loadWord(flags, operand));
                    if (divisor == 0) {
                            System.out.println("division by zero");
                    } else {
                            registers.setA(registers.getAs() / divisor);
                    }
                    break;
            case Opcode.AND:	registers.setA(registers.getA() & loadWord(flags, operand)); break;
            case Opcode.OR:		registers.setA(registers.getA() | loadWord(flags, operand)); break;
            case Opcode.COMP:	registers.setSWAfterCompare(registers.getAs() - SICXE.swordToInt(loadWord(flags, operand))); break;
            case Opcode.TIX:	registers.setX(registers.getX() + 1);
                                registers.setSWAfterCompare(registers.getXs() - SICXE.swordToInt(loadWord(flags, operand))); break;
            // input/output
            case Opcode.RD:		registers.setALo(devices.read(loadByte(flags, operand)));  break;
            case Opcode.WD:		devices.write(loadByte(flags, operand), registers.getALo()); break;
            case Opcode.TD:		registers.setSWAfterCompare(devices.test(loadByte(flags, operand)) ? -1 : 0); break;
            // floating point arithmetic
            case Opcode.ADDF:	registers.setF(registers.getF() + loadFloat(flags, operand)); break;
            case Opcode.SUBF:	registers.setF(registers.getF() - loadFloat(flags, operand)); break;
            case Opcode.MULF:	registers.setF(registers.getF() * loadFloat(flags, operand)); break;
            case Opcode.DIVF:	registers.setF(registers.getF() / loadFloat(flags, operand)); break;
            case Opcode.COMPF:  double sub = registers.getF() - loadFloat(flags, operand);
                                registers.setSWAfterCompare(sub > 0 ? 1 : (sub < 0 ? -1 : 0));
                                break;
            // others
            case Opcode.LPS:	notImplemented("LPS"); break;
            case Opcode.STI:	notImplemented("STI"); break;
            case Opcode.SSK:	notImplemented("SSK"); break;
            default: return false;
        }
        return true;
    }

    public int fetch() {
        int b = memory.getByteRaw(registers.getPC());
        registers.incPC();
        return b;
    }

    public void execute() throws DataBreakpointException {
        indirectX = false;
        instructionCount++;
        lastExecRead.clear();
        lastExecWrite.clear();
        lastExecAddr.setStartAddress(registers.getPC());
        lastExecAddr.setSpanLength(0);
        // fetch first byte
        int opcode = fetch();
        // try format 1
        if (execF1(opcode)) {
            lastExecAddr.setSpanLength(1);
            return;
        }
        // fetch one more byte
        int op = fetch();
        // try format 2
        if (execF2(opcode, op)) {
            lastExecAddr.setSpanLength(2);
            return;
        }
        // otherwise it is format SIC, F3 or F4
        Flags flags = new Flags(opcode, op);
        int instructionSize = 0;
        // operand depends on instruction format
        int operand;
        // check if standard SIC
        if (flags.isSic()) {
            operand = flags.operandSic(op, fetch());
            instructionSize = 3;
            // check if F4 (extended)
        } else if (flags.isExtended()) {
            operand = flags.operandF4(op, fetch(), fetch());
            if (flags.isRelative()) invalidAddressing();
            instructionSize = 4;
            // otherwise it is F3
        } else {
            instructionSize = 3;
            operand = flags.operandF3(op, fetch());
            if (flags.isPCRelative())
                operand = flags.operandPCRelative(operand) + registers.getPC();
            else if (flags.isBaseRelative())
                operand += registers.getB();
            else if (!flags.isAbsolute())
                invalidAddressing();  // both PC and base at the same time
        }
        // SIC, F3, F4 -- all support indexed addressing, but only when simple TA calculation used
        if (flags.isIndexed())
            if (flags.isSimple()) operand += registers.getXs();
            else if(flags.isIndirect()) indirectX = true;
            else invalidAddressing();
        // try to execute
        if (execSICF3F4(opcode & 0xFC, flags, operand)) {
            lastExecAddr.setSpanLength(instructionSize);
            return;
        }
        invalidOpcode(opcode);
    }


    // ********** Step over functionality *****************

    /**
     * Push the address bellow current JSUB to the stack, so we can step out of procedure later.
     */
    private void pushJSUB() {
        this.addressBelowJSUB.push(this.registers.getPC());
    }

    /**
     * Pop the last address bellow current JSUB from the stack, since we got out of current function.
     * (to be called with RSUB)
     */
    private void popJSUB() {
        this.addressBelowJSUB.pop();
    }

    /**
     * Get the address below the last JSUB was executed, so we can step out.
     * @return null if no item on stack - no JSUB encountered, otherwise last address.
     */
    public Integer getAddressBelowLastJSUB() {
        if (this.addressBelowJSUB.isEmpty()) return null;
        else return this.addressBelowJSUB.peek();
    }

}
