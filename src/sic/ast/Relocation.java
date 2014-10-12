package sic.ast;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Relocation {

    public final int address;       // address
    public final int length;       // length: 3 or 5
    public final char flag;         // flag: 0 (not used), '+' or '-'
    public final String symbol;     // used if flag != 0

    public Relocation(int address, int length, char flag, String symbol) {
        this.address = address;
        this.length = length;
        this.flag = flag;
        this.symbol = symbol;
    }

    public Relocation(int address, int length) {
        this(address, length, (char)0, null);
    }

    @Override
    public String toString() {
        return toModificationRecord();
    }

    public String toModificationRecord() {
        if (flag == 0) return String.format("M%06X%02X", address, length);
        return String.format("M%06X%02X%c%s", address, length, flag, symbol);
    }

    public boolean hasSymbol() {
        return flag != 0;  // or symbol != null
    }

}
