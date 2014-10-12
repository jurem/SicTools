package sic.ast.directives;

import sic.asm.*;
import sic.ast.Program;
import sic.ast.expression.Expr;
import sic.common.Mnemonic;

/**
 * Directive BASE: enable base-relative addressing.
 *
 * @author jure
 */
public class DirectiveBASE extends DirectiveFe {

    public DirectiveBASE(Location loc, String label, Mnemonic mnemonic, Expr expr) {
        super(loc, label, mnemonic, expr);
    }

    @Override
    public void enter(Program program) throws AsmError {
        program.section().enableBaseAddressing(value);
    }

}
