package sic.common;

/**
 * Flags ni and xbpe in F3/F4 format
 *
 * @author jure
 */
public class Flags {

    public static final int NONE         = 0x00;
    // ni flags
    public static final int MASK_NI      = 0x03;
    public static final int SIC          = 0x00;
    public static final int IMMEDIATE    = 0x01;
    public static final int INDIRECT     = 0x02;
    public static final int SIMPLE       = 0x03;
    // xbpe flags
    public static final int MASK_XBPE    = 0xF0;
    public static final int MASK_XBP     = 0xE0;
    public static final int MASK_BP      = 0x60;
    public static final int INDEXED      = 0x80;
    public static final int BASERELATIVE = 0x40;
    public static final int PCRELATIVE   = 0x20;
    public static final int EXTENDED     = 0x10;

    // flags
    private int ni;         // ...ni: only lower two bits are used
    private int xbpe;       // ...xbpe----: second four bits are used

    public Flags(int ni, int xbpe) {
        set_ni(ni);
        set_xbpe(xbpe);
    }

    public Flags() {
        this(Flags.NONE, Flags.NONE);
    }

    @Override
    public String toString() {
        String ni = "--";
        if (is_ni(SIMPLE)) ni = "ni";
        else if (is_ni(INDIRECT)) ni = "n-";
        else if (is_ni(IMMEDIATE)) ni = "-i";
        return ni +
            (isIndexed()      ? "x" : "-") +
            (isBaseRelative() ? "b" : "-") +
            (isPCRelative()   ? "p" : "-") +
            (isExtended()     ? "e" : "-");
    }

    public String operandToString(String operand) {
        if (isImmediate()) return "#" + operand;
        if (isIndirect()) return "@" + operand;
        if (isIndexed()) return operand + ",X";
        return operand;
    }

    // ************ ni

    public int get_ni() {
        return ni & MASK_NI;
    }

    public void set_ni(int what) {
        ni = what & MASK_NI;
    }

    public boolean is_ni(int what) {
        return (ni & MASK_NI) == what;
    }

    public boolean isSic() {
        return is_ni(SIC);
    }

    public boolean isImmediate() {
        return is_ni(IMMEDIATE);
    }

    public boolean isIndirect() {
        return is_ni(INDIRECT);
    }

    public boolean isSimple() {
        int f = ni & MASK_NI;
        return f == SIMPLE || f == SIC;
    }

    public byte combineWithOpcode(int opcode) {
        return (byte)(opcode & 0xFC | ni & MASK_NI);
    }

    // ************ xbpe

    public int get_xbpe() {
        return xbpe & MASK_XBPE;
    }

    public void set_xbpe(int xbpe) {
        this.xbpe = xbpe & MASK_XBPE;
    }

    public int get_x() {
        return (byte)(xbpe & 0x80);
    }

    public boolean isIndexed() {
        return (xbpe & INDEXED) == INDEXED;
    }

    public void setIndexed() {
        xbpe |= INDEXED;
    }

    public boolean isBaseRelative() {
        return (xbpe & BASERELATIVE) == BASERELATIVE;
    }

    public void setBaseRelative() {
        xbpe |= BASERELATIVE;
    }

    public boolean isPCRelative() {
        return (xbpe & PCRELATIVE) == PCRELATIVE;
    }

    public void setPCRelative() {
        xbpe |= PCRELATIVE;
    }

    public boolean isRelative() {
        return isPCRelative() || isBaseRelative();
    }

    public boolean isAbsolute() {
        return (xbpe & MASK_BP) == NONE;
    }

    public boolean isExtended() {
        return (xbpe & EXTENDED) == EXTENDED;
    }

    public void setExtended() {
        xbpe |= EXTENDED;
    }

    // ************ operands

    public int operandSic(int a, int b) {
        // 15-bit address
        return (a & 0x7F) << 8 | b & 0xFF;
    }

    public int operandF3(int a, int b) {
        // 12-bit address
        return (a & 0x0F) << 8 | b;
    }

    public int operandF4(int a, int b, int c) {
        // 20-bit address
        return (a & 0x0F) << 16 | b << 8 | c;
    }

    public int operandPCRelative(int op) {
        // 12-bit signed integer
        return op >= 2048 ? op - 4096 : op;
    }

    public int minOperand() {
        if (isExtended()) return SICXE.MIN_ADDR;
        else return isImmediate() ? SICXE.MIN_SDISP : SICXE.MIN_DISP;
    }

    public int maxOperand() {
        if (isExtended()) return SICXE.MAX_ADDR;
        else return SICXE.MAX_DISP;
    }

}
