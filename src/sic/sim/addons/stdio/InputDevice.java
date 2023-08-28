package sic.sim.addons.stdio;

import java.io.IOException;
import java.io.InputStream;

import sic.sim.vm.Device;

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
