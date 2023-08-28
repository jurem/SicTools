package sic.sim.vm;

import java.util.Vector;

import sic.common.Conversion;
import sic.common.Logger;
import sic.common.SICXE;
import sic.sim.addons.Addon;

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

    public void setDevices(Vector<Addon.AddonDevice> devices) {
        if (devices == null) {
            return;
        }
        for (Addon.AddonDevice d : devices) {
            setDevice(d.name, d.dev);
        }
    }

    public Devices(int count) {
        assert count > 2;
        devices = new Device[count];
        for (int i = 0; i < count; i++)
            setDevice(i, new FileDevice(Conversion.byteToHex(i) + ".dev"));
    }

}
