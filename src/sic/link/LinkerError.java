package sic.link;

import sic.link.section.Location;

/*
 * Errors that can be thrown in the linking process
 */
public class LinkerError extends Throwable {

    // just a String message
    public LinkerError(String message) {
        super(message);
    }

    // String message and location
    public LinkerError(String message, Location location) {
        super(message + " - " + location.toString());
    }


    //TODO

}
