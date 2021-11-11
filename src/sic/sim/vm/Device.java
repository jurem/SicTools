package sic.sim.vm;

/**
 * Base class for SIC/XE device.
 * @author jure
 */
public class Device {

    public boolean test() {
        // always true: EOF is returned by read
        return true;
    }

    public int read() {
        return -1;
    }

    public void write(int value) {
    }

    public void reset() {
    }

}
