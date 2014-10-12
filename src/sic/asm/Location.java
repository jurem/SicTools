package sic.asm;

/**
 * Location of a token, an error, etc. in the input.
 *
 * @author jure
 */
public class Location {

    public final int pos;
    public final int row;
    public final int col;

    public Location(int pos, int row, int col) {
        this.pos = pos;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return row + ", " + col;
    }

}
