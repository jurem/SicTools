package sic.sim.vm;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Device supporting writing to OutputStream, e.g. System.out.
 * @author jure
 */
public class OutputDevice extends Device {

	private OutputStream output;

	@Override
	public void write(int value) {
		try {
			output.write(value);
			output.flush();        // flush after each byte
		} catch (IOException e) {
		}
	}

    public OutputDevice(OutputStream output) {
        this.output = output;
    }

}
