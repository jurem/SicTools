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
public class DirectiveUSE extends Directive {

    public final String blockName;

    public DirectiveUSE(Location loc, String label, Mnemonic mnemonic, String blockName) {
        super(loc, label, mnemonic);
        this.blockName = blockName;
    }

    @Override
    public String operandToString() {
        return blockName;
    }

    @Override
    public void enter(Program program) throws AsmError {
        program.section().switchBlock(blockName);
        super.enter(program);
    }

}
