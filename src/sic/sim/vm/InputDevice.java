package sic.sim.vm;

import java.io.IOException;
import java.io.InputStream;

/**
 * Device which supports reading from InputStream, e.g. System.in
 * @author jure
 */
public class InputDevice extends Device {

    private InputStream input;

    @Override
    public int read() {
        try {
            return input.read();
        } catch (IOException e1) {
            return -1;
        }
    }

    @Override
    public boolean test() {
        try {
            return input.available() >= 1;
        } catch (IOException e1) {
            return false;
        }
    }

    public InputDevice(InputStream input) {
        this.input = input;
    }

}
