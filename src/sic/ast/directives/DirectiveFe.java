package sic.ast.directives;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.ast.Program;
import sic.common.Mnemonic;
import sic.ast.expression.Expr;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public abstract class DirectiveFe extends Directive {

    public final Expr expr;     // expression, can be null
    protected int value;        // value of the expression (when evaluated)

    public DirectiveFe(Location loc, String label, Mnemonic mnemonic, Expr expr) {
        super(loc, label, mnemonic);
        this.expr = expr;
    }

    @Override
    public String operandToString() {
        return (expr == null) ? "" : expr.toString();
    }

    public int value() {
        return value;
    }

    public void resolve(Program program) throws AsmError {
        value = expr == null ? -1 : expr.eval(program);
    }

}
