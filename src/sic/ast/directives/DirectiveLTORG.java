package sic.ast.directives;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.ast.Program;
import sic.common.Mnemonic;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class DirectiveLTORG extends Directive {

    public DirectiveLTORG(Location loc, String label, Mnemonic mnemonic) {
        super(loc, label, mnemonic);
    }

    @Override
    public void append(Program program) throws AsmError {
        super.append(program);
        program.section().literals.flush(program);
    }

}
