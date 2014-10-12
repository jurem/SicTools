package sic.ast;

import sic.asm.Location;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Comment extends Command {

    public Comment(Location loc, String comment) {
        super(loc, null, null);
        setComment(comment);
    }

    @Override
    public String toString() {
        return comment;
    }

    @Override
    public String operandToString() {
        return "";
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
    }

}
