package sic.ast.instructions;

import sic.asm.Location;
import sic.common.Mnemonic;
import sic.common.Conversion;

/**
 * Instruction in Format 2.
 *
 * @author jure
 */
public class InstructionF2rr extends InstructionF2Base {

    public final int register1;
    public final int register2;

    public InstructionF2rr(Location loc, String label, Mnemonic mnemonic, int register1, int register2) {
        super(loc, label, mnemonic);
        this.register1 = register1;
        this.register2 = register2;
    }

    @Override
    public String operandToString() {
        return Conversion.regToName(register1) + "," + Conversion.regToName(register2);
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        emitRawCode(data, loc, register1, register2);
    }

}
