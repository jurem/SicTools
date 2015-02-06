package sic.ast.instructions;

import sic.asm.Location;
import sic.common.Mnemonic;

/**
 * Instruction in Format 1.
 *
 * @author jure
 */
public class InstructionF1 extends Instruction {

    public InstructionF1(Location loc, String label, Mnemonic mnemonic) {
        super(loc, label, mnemonic);
    }

    @Override
    public String operandToString() {
        return "";
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        data[loc] = (byte)mnemonic.opcode;
    }

    @Override
    public String explain() {
        return Integer.toBinaryString(mnemonic.opcode);
    }
}
