package sic.ast.instructions;

import sic.asm.Location;
import sic.ast.Program;
import sic.ast.Symbol;
import sic.common.Flags;
import sic.common.Mnemonic;
import sic.asm.AsmError;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class InstructionF4m extends InstructionF34Base {

    public InstructionF4m(Location loc, String label, Mnemonic mnemonic, Flags flags, int operand, String symbol) {
        super(loc, label, mnemonic, flags, operand, symbol);
        flags.setExtended();
    }

    @Override
    protected void checkSymbol(Program program, Symbol symbol) throws AsmError {
    }

    @Override
    protected boolean resolveAddressing(Program program) throws AsmError {
        // relocate only realtive symbols
        if (resolvedSymbol != null) {
            if (resolvedSymbol.isImported())
                program.section().addRelocation(program.locctr() + 1, 5, '+', symbol);
            else
                program.section().addRelocation(program.locctr() + 1, 5);
        }
        return true;
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        data[loc]     = flags.combineWithOpcode(mnemonic.opcode);
        data[loc + 1] = (byte)(flags.get_xbpe() | (resolvedValue >> 16) & 0x0F);
        data[loc + 2] = (byte)((resolvedValue >> 8) & 0xFF);
        data[loc + 3] = (byte)(resolvedValue & 0xFF);
    }

}
