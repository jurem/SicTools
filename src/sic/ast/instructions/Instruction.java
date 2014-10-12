package sic.ast.instructions;

import sic.asm.Location;
import sic.ast.Command;
import sic.common.Mnemonic;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public abstract class Instruction extends Command {

    protected Instruction(Location loc, String label, Mnemonic mnemonic) {
        super(loc, label, mnemonic);
    }

}
