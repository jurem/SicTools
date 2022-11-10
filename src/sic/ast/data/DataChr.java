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
        StringBuilder buf = new StringBuilder();
        for (char c = parser.advance(); c != '\''; c = parser.advance()) {
            if (c == '\\') {
                c = parser.advance();
                switch (c) {
                    case '\'':
                        c = '\'';
                        break;
                    case '\\':
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 't':
                        c = '\t';
                        break;
                    case 'b':
                        c = '\b';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    case '0':
                        c = '\0';
                        break;
                    case 'x':
                        int mark = parser.pos();
                        parser.advance(2);
                        String str = parser.extract(mark);
                        try {
                            c = (char) Integer.parseInt(str, 16);
                        } catch (NumberFormatException e) {
                            throw new AsmError(parser.loc(), "Hexadecimal byte expected");
                        }
                        break;
                    default:
                        throw new AsmError(parser.loc(), "Unknown escape sequence '\\%c'", c);
                }
            }
            buf.append(c);
        }
        data = buf.toString().getBytes();
        if (allowList) super.parse(parser, true);
    }

}
