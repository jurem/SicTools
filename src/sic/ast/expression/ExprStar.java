package sic.ast.expression;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.asm.parsing.ExpressionParser;
import sic.ast.Program;

import java.util.Set;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class ExprStar extends Expr {

    public ExprStar(Location loc) {
        super("*", loc, 0);
    }

    @Override
    public String toString() {
        return "*";
    }

    @Override
    public Expr parse(ExpressionParser parser) throws AsmError {
        return this;
    }

    @Override
    public boolean hasSyms() {
        return false;
    }

    @Override
    public int countAddSub() {
        return 0;
    }

    @Override
    public Set<String> extractSyms() {
        return null;
    }

    @Override
    public boolean canEval(Program program) {
        return true;
    }

    @Override
    public int eval(Program program) {
        return program.locctr();
    }

}
