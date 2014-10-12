package sic.ast.instructions;

import sic.asm.Location;
import sic.common.Flags;
import sic.common.Mnemonic;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class InstructionF3 extends Instruction {

    private final Flags flags;

    public InstructionF3(Location loc, String label, Mnemonic mnemonic) {
        super(loc, label, mnemonic);
        flags = new Flags(Flags.SIMPLE, Flags.NONE);
    }

    @Override
    public String operandToString() {
        return "";
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        data[loc]     = flags.combineWithOpcode(mnemonic.opcode);
        data[loc + 1] = 0;
        data[loc + 2] = 0;
    }

}
