package sic.sim.vm;

import sic.common.*;

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

    // ************ Constructor

    public Machine() {
        this.registers = new Registers();
        this.memory = new Memory(MAX_ADDRESS+1);
        this.devices = new Devices(MAX_DEVICE+1);
    }

    // ************ getters/setters

    public int getInstructionCount() {
        return instructionCount;
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
            case Opcode.DIVR:	registers.set(o2, registers.gets(o2) / registers.gets(o1)); break;
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
    private int loadWord(Flags flags, int operand) {
        if (flags.isImmediate()) return operand;
        operand = memory.getWord(operand);
        if (flags.isIndirect()) operand = memory.getWord(operand);
        return operand;
    }

    private int loadByte(Flags flags, int operand) {
        if (flags.isImmediate()) return operand;
        if (flags.isIndirect()) return memory.getByte(memory.getWord(operand));
        return memory.getByte(operand);
    }

    private double loadFloat(Flags flags, int operand) {
        if (flags.isImmediate()) return operand;
        if (flags.isIndirect())  return memory.getFloat(memory.getWord(operand));
        return memory.getFloat(operand);
    }

    // use of TA for store: addr / addr of addr
    private int storeAddr(Flags flags, int addr) {
        return flags.isIndirect() ? memory.getWord(addr) : addr;
    }

    private boolean execSICF3F4(int opcode, Flags flags, int operand) {
        // Formats: SIC, F3, F4
        switch (opcode) {
            // ***** immediate addressing not possible *****
            // stores
            case Opcode.STA:	memory.setWord(storeAddr(flags, operand), registers.getA()); break;
            case Opcode.STX:	memory.setWord(storeAddr(flags, operand), registers.getX()); break;
            case Opcode.STL:	memory.setWord(storeAddr(flags, operand), registers.getL()); break;
            case Opcode.STCH:	memory.setByte(storeAddr(flags, operand), registers.getA()); break;
            case Opcode.STB:	memory.setWord(storeAddr(flags, operand), registers.getB()); break;
            case Opcode.STS:	memory.setWord(storeAddr(flags, operand), registers.getS());	break;
            case Opcode.STF:	memory.setFloat(storeAddr(flags, operand), registers.getF()); break;
            case Opcode.STT:	memory.setWord(storeAddr(flags, operand), registers.getT()); break;
            case Opcode.STSW:	memory.setWord(storeAddr(flags, operand), registers.getSW()); break;
            // jumps
            case Opcode.JEQ:	if (registers.isEqual()) registers.setPC(storeAddr(flags, operand)); break;
            case Opcode.JGT:	if (registers.isGreater()) registers.setPC(storeAddr(flags, operand)); break;
            case Opcode.JLT:	if (registers.isLower()) registers.setPC(storeAddr(flags, operand)); break;
            case Opcode.J:		registers.setPC(storeAddr(flags, operand)); break;
            case Opcode.RSUB:	registers.setPC(registers.getL()); break;
            case Opcode.JSUB:	registers.setL(registers.getPC()); registers.setPC(storeAddr(flags, operand)); break;
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
            case Opcode.DIV:	registers.setA(registers.getAs() / SICXE.swordToInt(loadWord(flags, operand))); break;
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
        int b = memory.getByte(registers.getPC());
        registers.incPC();
        return b;
    }

    public void execute() {
        instructionCount++;
        // fetch first byte
        int opcode = fetch();
        // try format 1
        if (execF1(opcode)) return;
        // fetch one more byte
        int op = fetch();
        // try format 2
        if (execF2(opcode, op)) return;
        // otherwise it is format SIC, F3 or F4
        Flags flags = new Flags(opcode, op);
        // operand depends on instruction format
        int operand;
        // check if standard SIC
        if (flags.isSic()) {
            operand = flags.operandSic(op, fetch());
            // check if F4 (extended)
        } else if (flags.isExtended()) {
            operand = flags.operandF4(op, fetch(), fetch());
            if (flags.isRelative()) invalidAddressing();
            // otherwise it is F3
        } else {
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
            else invalidAddressing();
        // try to execute
        if (execSICF3F4(opcode & 0xFC, flags, operand)) return;
        invalidOpcode(opcode);
    }

}
