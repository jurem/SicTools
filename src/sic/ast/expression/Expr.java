package sic.ast.expression;

import sic.asm.*;
import sic.asm.parsing.ExpressionParser;
import sic.ast.Program;

import java.util.Set;

// TODO: detection of absolute expressions, see p. 76

/**
 * TODO: write a short description
 *
 * @author jure
 */
public abstract class Expr {

    public final String name;   // name of the token as shown in syntax errors
    public final Location loc;  // location of the token
    public final int leftBP;    // left binding precedence

    public Expr(String name, Location loc, int leftBP) {
        this.name = name;
        this.loc = loc;
        this.leftBP = leftBP;
    }

    public Expr parse(ExpressionParser parser) throws AsmError {
        throw new AsmError(loc, "unexpected token '%s'", this);
    }

    public Expr parseLeft(ExpressionParser parser, Expr left) throws AsmError {
        throw new AsmError(loc, "unexpected token '%s'", this);
    }

    public abstract Set<String> extractSyms();

    public int countSyms() {
        Set<String> set = extractSyms();
        return set == null ? 0 : set.size();
    }

    public boolean hasSyms() {
        return countSyms() > 0;
    }

    public abstract int countAddSub();

    public abstract boolean canEval(Program program);

    public abstract int eval(Program program) throws AsmError;

}
