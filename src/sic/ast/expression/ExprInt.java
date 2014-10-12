package sic.ast.expression;

import sic.asm.*;
import sic.asm.parsing.ExpressionParser;
import sic.ast.Program;

import java.util.Set;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class ExprInt extends Expr {

    public final int value;

    public ExprInt(Location loc, int value) {
        super("<int>", loc, 0);
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Expr parse(ExpressionParser parser) throws AsmError {
        return this;
    }

    @Override
    public Set<String> extractSyms() {
        return null;
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
    public boolean canEval(Program program) {
        return true;
    }

    @Override
    public int eval(Program program) {
        return value;
    }

}
