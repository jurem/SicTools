package sic.ast.data;

import sic.asm.AsmError;
import sic.asm.parsing.Parser;
import sic.common.Conversion;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class DataHex extends Data {

    public DataHex(int opcode) {
        super(opcode);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(data.length + 3);
        buf.append("X'");
        buf.append(Conversion.bytesToHex(data, 0, data.length));
        buf.append('\'');
        buf.append(super.toString());
        return buf.toString();
    }

    @Override
    public void parse(Parser parser, boolean allowList) throws AsmError {
        parser.advance('X');
        parser.advance('\'');
        String str = parser.readUntil('\'');
        if (str.length() % 2 == 1)
            throw new AsmError(parser.loc(), "Invalid length of hex encoding '%s'", str);
        data = Conversion.hexToBytes(str);
        if (allowList) super.parse(parser, true);
    }

}
