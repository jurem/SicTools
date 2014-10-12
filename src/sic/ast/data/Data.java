package sic.ast.data;

import sic.asm.AsmError;
import sic.asm.parsing.Parser;
import sic.common.Opcode;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public abstract class Data {

    public final int opcode;    // the opcode of the corresponding storage directive
    protected byte[] data;      // actual data bytes
    protected Data next;        // linked list of Data-s

    public Data(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public String toString() {
        return (next == null) ? "" : "," + next.toString();
    }

    public void parse(Parser parser, boolean allowList) throws AsmError {
        if (allowList && parser.skipIfComma())
            next = parser.operandParser.parseData(opcode, true);
    }

    public boolean equals(Data that) {
        if (opcode != that.opcode) return false;  // distinguish BYTE,WORD,FLOaT
        if (data.length != that.data.length) return false;
        for (int i = 0; i < data.length; i++)
            if (data[i] != that.data[i]) return false;
        if (next == null && that.next == null) return true;
        if (next != null && that.next != null) return next.equals(that.next);
        return false;
    }

    public void setData(byte b) {
        data = new byte[1];
        data[0] = b;
    }

    protected int sizeHere() {
        switch (opcode) {
            case Opcode.BYTE:  return data.length;
            case Opcode.WORD:  return (data.length + 2) / 3 * 3;
            case Opcode.FLOT: return (data.length + 5) / 6 * 6;
        }
        return 0;
    }

    public int size() {
        int s = sizeHere();
        if (next != null) s += next.size();
        return s;
    }

    public void emit(byte[] data, int loc) {
        System.arraycopy(this.data, 0, data, loc, this.data.length);
        int s = sizeHere();
        for (int i = data.length; i < s; i++) data[i] = 0;
        if (next != null)
            next.emit(data, loc + s);
    }

}
