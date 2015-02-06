package sic.ast.instructions;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.ast.Program;
import sic.ast.Symbol;
import sic.common.Flags;
import sic.common.Mnemonic;

/**
 * Instruction in Format 3.
 *
 * @author jure
 */
public abstract class InstructionF34Base extends Instruction {

    // operand
    protected final Flags flags;        // flags nixbpe
    protected int value;                // if operand is a value
    protected String symbol;            // if operand is a symbol
    protected int resolvedValue;        // resolved value of the symbol
    protected Symbol resolvedSymbol;    // resolved symbol

    public InstructionF34Base(Location loc, String label, Mnemonic mnemonic, Flags flags, int value, String symbol) {
        super(loc, label, mnemonic);
        this.flags = flags;
        this.value = value;
        this.symbol = symbol;
        if (mnemonic.isExtended()) this.flags.setExtended();
    }

    public boolean operandIsValue() {
        return symbol == null;
    }

    @Override
    public String operandToString() {
        String op = operandIsValue() ? Integer.toString(value) : symbol;
        if (operandIsValue()) {
            if (flags.isPCRelative()) op = "(PC)" + (value >= 0 ? "+" : "") + op;
            else if (flags.isBaseRelative()) op = "(B)+" + op;
        }
        return flags.operandToString(op);
    }

    public void setSymbol(String symbol) {
        // used by literals
        this.symbol = symbol;
    }

    protected abstract void checkSymbol(Program program, Symbol symbol) throws AsmError;

    protected abstract boolean resolveAddressing(Program program) throws AsmError;

    public void resolve(Program program) throws AsmError {
        // resolve operand: value or symbol
        if (operandIsValue()) {
            resolvedValue = value;
            resolvedSymbol = null;
        } else {
            resolvedSymbol = program.section().symbols.get(symbol);
            if (resolvedSymbol == null)
                throw new AsmError(loc, "Undefined symbol '%s'", symbol);
            checkSymbol(program, resolvedSymbol);
            resolvedValue = resolvedSymbol.value();
        }
        // resolve addressing
        if (resolveAddressing(program)) return;
        // otherwise no suitable addressing found
        throw new AsmError(loc, "Cannot address symbol '%s'", symbol);
    }

    @Override
    public String explain() {
        String nixbpe = "<b>Bits nixbpe:</b> " + flags;
        return super.explain() + "<br>" + nixbpe;
    }

}
