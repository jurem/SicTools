package sic.asm;

/**
 * Syntax errors.
 *
 * @author jure
 */
public class AsmError extends Exception implements Comparable<AsmError> {

    public final Location loc;
    private boolean nonBreaking = false;

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

    public AsmError(Location loc, boolean nonBreaking, String format, Object... objs) {
        this(loc, String.format(format, objs));
        this.nonBreaking = nonBreaking;
    }

    @Override
    public String toString() {
        String severity = isBreaking() ? "Error" : "Warning";
        String head = severity + (loc != null ? " at " + loc + " (" + loc.pos + ")" : "");
        String message = this.getLocalizedMessage();
        return ((message != null) ? (head + ": " + message) : head) + ".";
    }

    @Override
    public int compareTo(AsmError that) {
        return this.loc.pos - that.loc.pos;
    }

    public boolean isBreaking() {
        return !nonBreaking;
    }

}
