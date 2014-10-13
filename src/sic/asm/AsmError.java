package sic.asm;

/**
 * Syntax errors.
 *
 * @author jure
 */
public class AsmError extends Exception implements Comparable<AsmError> {

    public final Location loc;

    public AsmError(Location loc, String msg) {
        super(msg);
        this.loc = loc;
    }

    public AsmError(Location loc, String format, Object... objs) {
        this(loc, String.format(format, objs));
    }

    public AsmError(String format, Object... objs) {
        this(null, format, objs);
    }

    @Override
    public String toString() {
        String head = "Error" + (loc != null ? " at " + loc + " (" + loc.pos + ")" : "");
        String message = this.getLocalizedMessage();
        return ((message != null) ? (head + ": " + message) : head) + ".";
    }

    @Override
    public int compareTo(AsmError that) {
        return this.loc.pos - that.loc.pos;
    }

}
