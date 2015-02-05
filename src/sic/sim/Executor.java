package sic.sim;

import sic.sim.vm.Machine;

import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jure
 */
// TODO: rename class
public class Executor {

    public final Machine machine;
    private Timer timer;
    private int speedkHz = 10;
    public final Breakpoints breakpoints;
    public ActionListener onBreakpoint;
    private boolean hasChanged;

    public Executor(Machine machine) {
        this.machine = machine;
        this.breakpoints = new Breakpoints();
    }

    public Machine getMachine() {
        return machine;
    }

    public int getSpeed() {
        return speedkHz;
    }

    public void setSpeed(int kHz) {
        if (kHz > 10000) kHz = 10000;
        speedkHz = kHz;
    }

    public void start() {
        if (timer != null) return;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < speedkHz; i++) {
                    int oldPC = machine.registers.getPC();
                    machine.execute();
                    hasChanged = true;
                    // check if the same instruction: halt J halt
                    if (oldPC == machine.registers.getPC()) {
                        stop();
                        break;
                    }
                    // check breakpoints
                    if (breakpoints.has(machine.registers.getPC())) {
                        stop();
                        if (onBreakpoint != null) onBreakpoint.actionPerformed(null);
                        break;
                    }
                }
            }
        }, 0, 1);
    }

    public void stop() {
        if (timer == null) return;
        timer.cancel();
        timer = null;
    }

    public void step() {
        if (!isRunning()) {
            machine.execute();
            hasChanged = true;
        }
    }

    public boolean isRunning() {
        return timer != null;
    }

    public boolean hasChanged() {
        boolean c = hasChanged;
        hasChanged = false;
        return c;
    }
}
