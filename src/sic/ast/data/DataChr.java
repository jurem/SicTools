package sic.ast.data;

import sic.asm.AsmError;
import sic.asm.parsing.Parser;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class DataChr extends Data {

    public DataChr(int opcode) {
        super(opcode);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(data.length + 3);
        buf.append("C'");
        for (byte b : data) buf.append((char) b);
        buf.append('\'');
        return buf.toString() + super.toString();
    }

    @Override
    public void parse(Parser parser, boolean allowList) throws AsmError {
        parser.advance('C');
        parser.advance('\'');
        data = parser.readUntil('\'').getBytes();
        if (allowList) super.parse(parser, true);
    }

}
