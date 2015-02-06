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
    public static final int MaxSpeed = 100000000; // Hz

    public final Machine machine;
    private Timer timer;
    private int timerPeriod;        // timer period in miliseconds
    private int timerRepeat;        // timer loop-repeat count
    public final Breakpoints breakpoints;
    public ActionListener onBreakpoint;
    private boolean hasChanged;

    public Executor(final Machine machine) {
        this.machine = machine;
        this.breakpoints = new Breakpoints();
        setSpeed(100);
    }

    public Machine getMachine() {
        return machine;
    }

    public int getSpeed() {
        return 1000 / timerPeriod * timerRepeat;
    }

    public void setSpeed(int Hz) {
        if (Hz > MaxSpeed) Hz = MaxSpeed;
        timerRepeat = (Hz + 100) / 100;
        timerPeriod = 1000 * timerRepeat / Hz;
    }

    private void timerTick() {
        for (int i = 0; i < timerRepeat; i++) {
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

    public void start() {
        if (timer != null) return;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerTick();
            }
        }, 0, timerPeriod);
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
