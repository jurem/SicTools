package sic.ast.storage;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.ast.Program;
import sic.ast.expression.Expr;
import sic.common.Mnemonic;
import sic.common.Opcode;

import java.util.Arrays;

/**
 * Support for storage-reservation directives. This class supports both standard
 * directives: RESB (reserve bytes) and RESW (reserver words).
 * In addition, it also supports RESF (reserve floats).
 *
 * @author jure
 */
public class StorageRes extends Storage {

    public final Expr expr;  // expression
    private int count;  // value of expression

    public StorageRes(Location loc, String label, Mnemonic mnemonic, Expr expr) {
        super(loc, label, mnemonic);
        this.expr = expr;
    }

    @Override
    public String operandToString() {
        return expr.toString();
    }

    @Override
    public int size() {
        switch (mnemonic.opcode) {
            case Opcode.RESB: return count;
            case Opcode.RESW: return 3 * count;
            case Opcode.RESF: return 6 * count;
        }
        return 0;  // error
    }

    public void resolve(Program program) throws AsmError {
        count = expr.eval(program);
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        int s = size();
        if (s <= 0) return;
        // fill with zeros?
        Arrays.fill(data, loc, loc + size() - 1, (byte)0);
    }

    @Override
    public boolean emitText(StringBuilder buf) {
        return true; // no text emitted, but indicate flush
    }

}
