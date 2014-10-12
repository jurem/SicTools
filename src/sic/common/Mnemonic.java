package sic.common;

/**
 * Base abstract class for map.
 *
 * @author jure
 */
public class Mnemonic {

    public final String name;        // name of the mnemonic
    public final int opcode;         // operation code
    public final Format format;      // operand format
    public final String hint;        // usage hint
    public final String desc;        // short description

    public Mnemonic(String name, int opcode, Format format, String hint, String desc) {
        this.name = name;
        this.opcode = opcode;
        this.format = format;
        this.hint = hint;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isExtended() {
        return name.startsWith("+");
    }

}
