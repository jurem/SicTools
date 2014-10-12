package sic.ast.instructions;

import sic.asm.Location;
import sic.common.Mnemonic;

/**
 * Instruction in Format 2.
 *
 * @author jure
 */
public class InstructionF2n extends InstructionF2Base {

    public final int number;

    public InstructionF2n(Location loc, String label, Mnemonic mnemonic, int number) {
        super(loc, label, mnemonic);
        this.number = number;
    }

    @Override
    public String operandToString() {
        return Integer.toString(number);
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        emitRawCode(data, loc, number, 0);
    }

}
