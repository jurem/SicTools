package sic.sim.breakpoints;

import java.util.ArrayList;
import java.util.Iterator;

public class DataBreakpoints {
    private ArrayList<DataBreakpoint> breakpoints = new ArrayList<>();

    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public Iterator<DataBreakpoint> getBreakpointsIterator() {
        return breakpoints.iterator();
    }

    /**
     * Check if any of the breakpoints is triggered on given address
     * Throws exception if breakpoint is triggered.
     * @param address Address to check
     */
    public void checkRead(int address) throws ReadDataBreakpointException {
        if (!this.enabled) return;

        for (DataBreakpoint breakpoint : breakpoints) {
            if (breakpoint.checkRead(address)) {
                this.disable(); // let next instruction through
                throw new ReadDataBreakpointException(breakpoint, address);
            }
        }
    }

    /**
     * Check if any of the breakpoints is triggered on given address.
     * Throws exception if breakpoint is triggered.
     * @param address Address to check
     */
    public void checkWrite(int address) throws WriteDataBreakpointException {
        if (!this.enabled) return;

        for (DataBreakpoint breakpoint : breakpoints) {
            if (breakpoint.checkWrite(address)) {
                this.disable(); // let next instruction through
                throw new WriteDataBreakpointException(breakpoint, address);
            }
        }
    }

    public void add(DataBreakpoint breakpoint) {
        this.breakpoints.add(breakpoint);
    }

    public void remove(DataBreakpoint breakpoint) {
        this.breakpoints.remove(breakpoint);
    }

    public void remove(int breakpointIndex) {
        this.breakpoints.remove(breakpointIndex);
    }

    public DataBreakpoint at(int index) {
        return this.breakpoints.get(index);
    }


}


