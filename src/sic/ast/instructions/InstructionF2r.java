package sic.ast.instructions;

import sic.asm.Location;
import sic.common.Mnemonic;
import sic.common.Conversion;

/**
 * Instruction in Format 2.
 *
 * @author jure
 */
public class InstructionF2r extends InstructionF2Base {

    public final int register;

    public InstructionF2r(Location loc, String label, Mnemonic mnemonic, int register) {
        super(loc, label, mnemonic);
        this.register = register;
    }

    @Override
    public String operandToString() {
        return Conversion.regToName(register);
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        emitRawCode(data, loc, register, 0);
    }

}
