package sic.ast.directives;

import sic.asm.*;
import sic.ast.Program;
import sic.ast.expression.Expr;
import sic.common.Mnemonic;

/**
 * Directive END: end program and define program entry address.
 *
 * @author jure
 */
public class DirectiveEND extends DirectiveFe {

    public DirectiveEND(Location loc, String label, Mnemonic mnemonic, Expr expr) {
        super(loc, label, mnemonic, expr);
    }

    @Override
    public void enter(Program program) throws AsmError {
        program.switchDefault();
        super.enter(program);
    }

    @Override
    public void append(Program program) throws AsmError {
        program.flushAllLiterals();
        super.append(program);
    }

}
