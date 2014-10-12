package sic.asm.visitors;

import sic.asm.AsmError;
import sic.asm.ErrorCatcher;
import sic.ast.Command;
import sic.ast.Program;
import sic.ast.Symbol;
import sic.ast.directives.DirectiveEQU;
import sic.ast.expression.ExprStar;

/**
 * Evaluate EQU (absolute or relative) expressions.
 * Use the algorithm from the book for forward reference resolution.
 *
 * @author jure
 */
public class EvalEQUs extends Visitor {

    public final boolean relative;

    public EvalEQUs(Program program, ErrorCatcher errorCatcher, boolean relative) {
        super(program, errorCatcher);
        this.relative = relative;
    }

    public void visit(Command c) throws AsmError {
        if (relative && c.hasLabel())
            program.section().symbols.notify(program, c.label());
    }

    public void visit(DirectiveEQU d) throws AsmError {
        if (d.expr instanceof ExprStar && !relative) return; // TODO: ugly

        program.section().symbols.update(program, d.label());
        Symbol sym = program.section().symbols.get(d.label());
        if (!sym.isEvaluated()) return;
        // otherwise sym was successfully evaluated on this step
        // update sym: if absolute step or if relative symbol is indeed absolute
        if (!relative || d.expr.countAddSub() == 0) sym.setAbsolute(true);
    }

}