package sic.ast.instructions;

import sic.asm.Location;
import sic.common.Mnemonic;
import sic.common.Conversion;

/**
 * Instruction in Format 2.
 *
 * @author jure
 */
public class InstructionF2rn extends InstructionF2Base {

    public final int register;
    public final int number;

    public InstructionF2rn(Location loc, String label, Mnemonic mnemonic, int register, int number) {
        super(loc, label, mnemonic);
        this.register = register;
        this.number = number;
    }

    @Override
    public String operandToString() {
        return Conversion.regToName(register) + "," + Integer.toString(number);
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        emitRawCode(data, loc, register, number - 1);
    }

}
