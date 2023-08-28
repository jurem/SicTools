package rand;

import java.util.Random;

import sic.sim.vm.Device;

public class RandDevice extends Device {
    private Random random = new Random();

    @Override
    public int read() {
        return random.nextInt(256);
    }
}
