package sic.sim.vm;

import sic.common.Conversion;
import sic.common.Logger;
import sic.common.SICXE;

/**
 * @author jure
 */
public class Devices {

    private Device[] devices;

    public Device getDevice(int idx) {
        return devices[idx];
    }

    public void setDevice(int idx, Device device) {
        devices[idx] = device;
    }

    // returns true if invalid
    private boolean checkDeviceIndex(int idx) {
        boolean invalid = idx < 0 || idx >= devices.length;
        if (invalid) Logger.fmterr("Invalid device number '%d'.", idx);
        return invalid;
    }

    public int read(int idx) {
        if (checkDeviceIndex(idx)) {
            Logger.fmterr("Invalid device number '%d'.", idx);
            return 0;
        }
        int val = devices[idx].read();
        if (val < 0 || val > 255) val = 0;
        return val;
    }

    public void write(int idx, int val) {
        if (checkDeviceIndex(idx))
            Logger.fmterr("Invalid device number '%d'.", idx);
        else
            devices[idx].write(val & 0xFF);
    }

    public boolean test(int idx) {
        if (checkDeviceIndex(idx)) {
            Logger.fmterr("Invalid device number '%d'.", idx);
            return false;
        }
        return devices[idx].test();
    }

    public void reset() {
        for (int i = SICXE.DEVICE_FREE; i < devices.length; i++) {
            devices[i].reset();
        }
    }

    public Devices(int count) {
        assert count > 2;
        devices = new Device[count];
        setDevice(SICXE.DEVICE_STDIN, new InputDevice(System.in));
        setDevice(SICXE.DEVICE_STDOUT, new OutputDevice(System.out));
        setDevice(SICXE.DEVICE_STDERR, new OutputDevice(System.err));
        for (int i = SICXE.DEVICE_FREE; i < count; i++)
            setDevice(i, new FileDevice(Conversion.byteToHex(i) + ".dev"));
    }

}
