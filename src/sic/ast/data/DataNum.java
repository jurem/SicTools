package sic.ast.data;

import sic.asm.AsmError;
import sic.asm.parsing.Parser;
import sic.common.Opcode;
import sic.common.SICXE;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class DataNum extends Data {

    private int numint;
    private double numfloat;

    public DataNum(int opcode) {
        super(opcode);
    }

    @Override
    public String toString() {
        switch (opcode) {
            case Opcode.BYTE:
            case Opcode.WORD: return Integer.toString(numint) + super.toString();
            case Opcode.FLOT: return Double.toString(numfloat) + super.toString();
        }
        return "";
    }

    @Override
    public void parse(Parser parser, boolean allowList) throws AsmError {
        switch (opcode) {
            case Opcode.BYTE:
                numint = parser.readInt(SICXE.MIN_SBYTE, SICXE.MAX_BYTE);
                data = SICXE.intToDataByte(numint);
                break;
            case Opcode.WORD:
                numint = parser.readInt(SICXE.MIN_SWORD, SICXE.MAX_WORD);
                data = SICXE.intToDataWord(numint);
                break;
            case Opcode.FLOT:
                numfloat = parser.readFloat();
                data = SICXE.doubleToDataFloat(numfloat);
                break;
        }
        if (allowList) super.parse(parser, true);
    }

}
