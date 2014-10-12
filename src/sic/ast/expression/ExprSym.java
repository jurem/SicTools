package sic.ast.expression;

import sic.asm.*;
import sic.asm.parsing.ExpressionParser;
import sic.ast.Program;
import sic.ast.Symbols;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class ExprSym extends Expr {

    public final String value;

    public ExprSym(Location loc, String value) {
        super("<sym>", loc, 0);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Expr parse(ExpressionParser parser) throws AsmError {
        return this;
    }

    @Override
    public Set<String> extractSyms() {
        Set<String> set = new LinkedHashSet<String>();
        set.add(value);
        return set;
    }

    @Override
    public boolean hasSyms() {
        return true;
    }

    @Override
    public int countAddSub() {
        return 1;
    }

    @Override
    public boolean canEval(Program program) {
        return program.section().symbols.isEvaluated(value);
    }

    @Override
    public int eval(Program program) throws AsmError {
        Symbols symbols = program.section().symbols;
        if (symbols.isEvaluated(value)) return symbols.get(value).value();
        throw new AsmError(loc, "Undefined symbol '%s'", value);
    }

}
