package sic.ast.directives;

import sic.asm.Location;
import sic.ast.Command;
import sic.common.Mnemonic;

/**
 * Base class for assembler directives including storage directives.
 *
 * @author jure
 */
public abstract class Directive extends Command {

    public Directive(Location loc, String label, Mnemonic mnemonic) {
        super(loc, label, mnemonic);
    }

    @Override
    public String operandToString() {
        return "";  // default
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        // directives emit no code
    }

}
