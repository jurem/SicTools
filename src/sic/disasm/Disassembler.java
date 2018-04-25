package sic.disasm;

import sic.asm.Location;
import sic.ast.data.Data;
import sic.ast.data.DataHex;
import sic.ast.storage.StorageData;
import sic.common.*;
import sic.ast.Command;
import sic.ast.instructions.*;
import sic.sim.vm.Machine;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Disassembler {

    private final Mnemonics mnemonics;
    private final Machine machine;

    private int location;

    public Disassembler(Mnemonics mnemonics, Machine machine) {
        this.mnemonics = mnemonics;
        this.machine = machine;
    }

    public int location() {
        return location;
    }

    public void setLocation(int location) {
        if (location < 0) location = 0;
        if (location > SICXE.MAX_ADDR) location = SICXE.MASK_ADDR;
        this.location = location;
    }

    public void gotoPC() {
        setLocation(machine.registers.getPC());
    }

    public void next() {
        Command cmd = disassemble(location);
        setLocation(location + (cmd == null ? 1 : cmd.size()));
    }

    public void prev() {
        int l = location;
        Command cmd;
        do {
            cmd = disassemble(--l);
        } while (cmd == null || l + cmd.size() > location);
        setLocation(l);
    }

    public void next(int count) {
        while (count-- > 0) next();
    }

    public void prev(int count) {
        while (count-- > 0) prev();
    }

    private int fetchAddr;
    protected int fetch() {
        if (fetchAddr < 0) return 0;
        if (fetchAddr > SICXE.MAX_ADDR) return 0;
        return machine.memory.getByteRaw(fetchAddr++);
    }

    public Instruction disassemble(int addr) {
        this.fetchAddr = addr;
        int opcode = fetch();
        String name = Opcode.getName(opcode & 0xFC);
        if (name == null) return null;
        Mnemonic mnemonic = mnemonics.get(name);
        int b1, b2;
        Location loc = new Location(-1,-1,-1);
        switch (mnemonic.format) {
            case F1:
                return new InstructionF1(loc, "", mnemonic);
            case F2n:
                return new InstructionF2n(loc, "", mnemonic, fetch() >> 4);
            case F2r:
                return new InstructionF2r(loc, "", mnemonic, fetch() >> 4);
            case F2rn:
                b1 = fetch();
                return new InstructionF2rn(loc, "", mnemonic, (b1 & 0xF0) >> 4, (b1 & 0x0F) + 1);
            case F2rr:
                b1 = fetch();
                return new InstructionF2rr(loc, "", mnemonic, (b1 & 0xF0) >> 4, b1 & 0x0F);
            case F3:
                fetch(); fetch(); // should be zero?
                return new InstructionF3(loc, "", mnemonic);
            case F3m:
            case F4m:
                b1 = fetch(); b2 = fetch();
                Flags flags = new Flags(opcode, b1);
                if (flags.isExtended()) {
                    int operand = flags.operandF4(b1, b2, fetch());
                    mnemonic = mnemonics.get("+" + name);
                    return new InstructionF4m(loc, "", mnemonic, flags, operand, null);
                }
                // F3 or Sic
                int operand = flags.isSic() ? flags.operandSic(b1, b2) : flags.operandF3(b1, b2);
                if (flags.isPCRelative()) operand = flags.operandPCRelative(operand);
                return new InstructionF3m(loc, "", mnemonic, flags, operand, null);
        }
        return null;
    }

    public Command disassembleSafe(int loc) {
        Instruction instruction = disassemble(loc);
        if (instruction == null) {
            Data data = new DataHex(mnemonics.get("BYTE").opcode);
            data.setData((byte)machine.memory.getByteRaw(loc));
            return new StorageData(new Location(-1,-1,-1), "", mnemonics.get("BYTE"), data);
        }
        return instruction;
    }

    /**
     * Returns the location after given PC
     * @param location (current) address
     * @return next instruction address
     */
    public int getLocationAfter(int location) {
        Command cmd = disassemble(location);
        return location + (cmd == null ? 1 : cmd.size());
    }

    /**
     * Get the next PC location, ignoring jumps / function calls.
     * @return next instruction address
     */
    public int getNextPCLocation() {
        return getLocationAfter(machine.registers.getPC());
    }

}