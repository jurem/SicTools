package sic.asm.visitors;

import sic.asm.AsmError;
import sic.asm.ErrorCatcher;
import sic.ast.Program;
import sic.ast.directives.DirectiveEQU;

/**
 * Define symbols declared with EQU directive.
 *
 * @author jure
 */
public class DefineEQUs extends Visitor {

    public DefineEQUs(Program program, ErrorCatcher errorCatcher) {
        super(program, errorCatcher);
    }

    public void visit(DirectiveEQU d) throws AsmError {
        program.section().symbols.defineEQU(d.label(), d.loc, d.expr);
    }

}