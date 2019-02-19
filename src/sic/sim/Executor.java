package sic.sim;

import sic.sim.breakpoints.Breakpoints;
import sic.sim.breakpoints.DataBreakpointException;
import sic.sim.breakpoints.DataBreakpoints;
import sic.sim.vm.Machine;

import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

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
    private final DataBreakpoints dataBreakpoints;
    public ActionListener onBreakpoint;
    private boolean hasChanged;

    private boolean printStats = false;

    public Executor(final Machine machine) {
        this.machine = machine;
        this.breakpoints = new Breakpoints();
        this.dataBreakpoints = machine.memory.dataBreakpoints;
        this.dataBreakpoints.enable();
        setSpeed(100);
    }

    public Executor(final Machine machine, Args arg) {
        this(machine);

        this.printStats = arg.isStats();
        if (arg.getFreq() > 0) setSpeed(arg.getFreq());
    }

    public Machine getMachine() {
        return machine;
    }

    public int getSpeed() {
        return 1000 / timerPeriod * timerRepeat;
    }

    public void setSpeed(int Hz) {
        if (Hz > MaxSpeed) Hz = MaxSpeed;
        timerRepeat = (Hz + 99) / 100;
        timerPeriod = 1000 * timerRepeat / Hz;
    }

    /**
     * Execute the timer tick until the stopPredicate / Breakpoint / Halt is reached.
     * @param stopPredicate Stop if predicate passes.
     */
    private void timerTickUntil(Predicate<Machine> stopPredicate) {
        for (int i = 0; i < timerRepeat; i++) {
            int oldPC = machine.registers.getPC();

            try {
                machine.execute();

                if (!dataBreakpoints.isEnabled()) {
                    // Enable data breakpoints in case they got disabled because they were triggered.
                    dataBreakpoints.enable();
                }
            } catch (DataBreakpointException ex) {
                machine.registers.setPC(oldPC); // reset PC to old one - instruction didn't execute anyway
                hasChanged = true;
                stop();
                if (onBreakpoint != null) onBreakpoint.actionPerformed(null);
                break;
            }

            hasChanged = true;
            // check if the same instruction: halt J halt
            if (oldPC == machine.registers.getPC()) {
                stop();
                if (printStats) {
                    System.out.printf("Instructions executed: %d\n", machine.getInstructionCount());
                }
                break;
            }
            // check breakpoints
            if (breakpoints.has(machine.registers.getPC())) {
                stop();
                if (onBreakpoint != null) onBreakpoint.actionPerformed(null);
                break;
            }

            if (stopPredicate.test(machine)) {
                stop();
                break;
            }
        }
    }

    /**
     * Run until the stopPredicate / Breakpoint / Halt is reached.
     * @param stopPredicate Stop if predicate passes.
     */
    private void runUntil(Predicate<Machine> stopPredicate) {
        if (timer != null) return;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerTickUntil(stopPredicate);
            }
        }, 0, timerPeriod);
    }

    public void start() {
        Predicate<Machine> stopPredicate = x -> false; // Never stop - no additional stop condition
        runUntil(stopPredicate);
    }

    public void stop() {
        if (timer == null) return;
        timer.cancel();
        timer = null;
    }

    public void step() {
        if (!isRunning()) {

            boolean dataBpEnabledBefore = dataBreakpoints.isEnabled();
            dataBreakpoints.disable();

            try {
                machine.execute();
            } catch (DataBreakpointException ex) {
                // Shouldn't be triggered when breakpoints are disabled
            }

            if (dataBpEnabledBefore) dataBreakpoints.enable();

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

    /**
     * Start the machine and run until the given address is reached in PC (or breakpoint or halt).
     * @param stopAddress Address to stop at.
     */
    public void runToAddress(int stopAddress) {
        runUntil(machine -> machine.registers.getPC() == stopAddress);
    }

    /**
     * Step out of the current sub procedure
     */
    public void stepOut() {
        Integer addressAfterLastJSUB = machine.getAddressBelowLastJSUB();
        if (addressAfterLastJSUB == null) return;
        runUntil(machine -> machine.registers.getPC() == addressAfterLastJSUB);
    }
}
