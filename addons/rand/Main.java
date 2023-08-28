package rand;

import java.util.Vector;

import sic.sim.addons.Addon;
import sic.sim.Executor;

public class Main extends Addon {
    private int device = 3;
    public void load(String args) {
        System.out.println("Loading rand");
        if (args != null) {
            device = Integer.parseInt(args);
        }
    }

    @Override
    public Vector<AddonDevice> getDevices() {
        Vector<AddonDevice> vc = new Vector<AddonDevice>();
        vc.add(new Addon.AddonDevice(device, new RandDevice(), true));
        return vc;
    }
}
