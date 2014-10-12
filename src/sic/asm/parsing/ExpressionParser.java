package sic.asm.parsing;

import sic.asm.AsmError;
import sic.ast.expression.Expr;
import sic.ast.expression.ExprInt;
import sic.ast.expression.ExprOp;
import sic.ast.expression.ExprSym;
import sic.asm.Location;
import sic.common.SICXE;

/**
 * Support for parsing expressions.
 *
 * @author jure
 */
public class ExpressionParser {

    private final Parser parser;
    public final boolean allowSymbols;

    public ExpressionParser(Parser parser, boolean allowSymbols) {
        this.parser = parser;
        this.allowSymbols = allowSymbols;
    }

    private Expr readToken() throws AsmError {
        parser.skipWhitespace();
        Location loc = parser.loc();
        if (Character.isDigit(parser.peek()))
            return new ExprInt(loc, parser.readInt(0, SICXE.MAX_WORD));
        if (allowSymbols && Character.isLetter(parser.peek()))
            return new ExprSym(loc, parser.readSymbol());
        if (parser.advanceIf('+'))
            return new ExprOp("+", loc, 10);
        if (parser.advanceIf('-'))
            return new ExprOp("-", loc, 10);
        if (parser.advanceIf('*'))
            return new ExprOp("*", loc, 20);
        if (parser.advanceIf('/'))
            return new ExprOp("/", loc, 20);
        if (parser.advanceIf('%'))
            return new ExprOp("%", loc, 20);
        return null;  // end of expression
    }

    private Expr nextTok;

    public Expr parseExpression() throws AsmError {
        nextTok = readToken();
        if (nextTok == null) return null;
        return parseExpression(0);
    }

    public Expr parseExpression(int rightBP) throws AsmError {
        Expr tok = nextTok;
        nextTok = readToken();
        Expr left = tok.parse(this);
        while (nextTok != null && nextTok.leftBP > rightBP) {
            tok = nextTok;
            nextTok = readToken();
            left = tok.parseLeft(this, left);
        }
        return left;
    }

}
