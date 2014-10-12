package sic.ast.storage;

import sic.asm.Location;
import sic.ast.Command;
import sic.common.Mnemonic;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public abstract class Storage extends Command {

    public Storage(Location loc, String label, Mnemonic mnemonic) {
        super(loc, label, mnemonic);
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        // most of the directives emit no code
    }

}
