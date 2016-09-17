package sic.link;

import sic.link.section.Location;

/*
 * Errors that can be thrown in the linking process
 */
public class LinkerError extends Throwable {

    // empty message
    public LinkerError() {
        super("");
    }

    // just a String message
    public LinkerError(String phase, String msg) {
        super(phase + ": " + msg);
    }

    // String message and Location
    public LinkerError(String phase, String msg, Location loc) {
        super(phase + ": " + msg + " - " + loc.toString());
    }

}
