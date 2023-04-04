package sic.sim.addons.stdio;

import java.util.Vector;

import sic.sim.addons.Addon;
import sic.sim.Executor;
import sic.common.SICXE;

public class Main extends Addon {
    @Override
    public Vector<AddonDevice> getDevices() {
        Vector<AddonDevice> vc = new Vector<AddonDevice>();
        vc.add(new Addon.AddonDevice(SICXE.DEVICE_STDIN, new InputDevice(System.in), true));
        vc.add(new Addon.AddonDevice(SICXE.DEVICE_STDOUT, new OutputDevice(System.out), true));
        vc.add(new Addon.AddonDevice(SICXE.DEVICE_STDERR, new OutputDevice(System.err), true));
        return vc;
    }
}
