package sic.ast.data;

import sic.asm.AsmError;
import sic.asm.parsing.Parser;
import sic.common.Conversion;
import sic.common.Opcode;
import sic.common.SICXE;

public class DataFloat extends Data {

    private double num;

    public DataFloat(int opcode) {
        super(opcode);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(data.length + 3);
        buf.append("F'");
        buf.append(Conversion.dataToFloat(data));
        buf.append('\'');
        return buf.toString() + super.toString();
    }

    @Override
    public void parse(Parser parser, boolean allowList) throws AsmError {
        parser.advance('F');
        parser.advance('\'');
        num = parser.readFloat();
        data = SICXE.doubleToDataFloat(num);
        parser.advance('\'');
        if (allowList) super.parse(parser, true);
    }

}
