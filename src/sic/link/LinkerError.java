package sic.link;

import sic.link.section.Location;

/*
 * Errors that can be thrown in the linking process
 */
public class LinkerError extends Throwable {

    // empty message
    public LinkerError(Void v) {
        super("");
    }

    // just a String message
    public LinkerError(String phase, String message) {
        super(phase + ": " + message);
    }

    // String message and location
    public LinkerError(String phase, String message, Location location) {
        super(phase + ": " + message + " - " + location.toString());
    }


    //TODO

}
