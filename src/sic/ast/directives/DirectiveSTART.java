package sic.ast.directives;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.ast.Program;
import sic.ast.expression.Expr;
import sic.common.Mnemonic;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class DirectiveSTART extends DirectiveFe {

    public DirectiveSTART(Location loc, String label, Mnemonic mnemonic, Expr expr) {
        super(loc, label, mnemonic, expr);
    }

    @Override
    public void append(Program program) throws AsmError {
        if (!hasLabel())
            throw new AsmError(loc, "Missing label at START");
        super.append(program);
    }

}
