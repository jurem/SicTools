package sic.sim.breakpoints;

import java.util.ArrayList;

public class MemoryBreakpoints {
    private ArrayList<MemoryBreakpoint> breakpoints = new ArrayList<>();

    public MemoryBreakpoint triggered;

    public Integer ignoreNext;

    public MemoryBreakpoints() {

    }

    /**
     * Check if any of the breakpoints is triggered on given address
     * Throws exception if breakpoint is triggered.
     * @param address Address to check
     */
    public void checkRead(int address) throws ReadMemoryBreakpointException {
        if (ignoreNext != null && ignoreNext == address) {
            ignoreNext = null;
            return;
        }

        for (MemoryBreakpoint breakpoint : breakpoints) {
            if (breakpoint.checkRead(address)) {
                ignoreNext = address;
                throw new ReadMemoryBreakpointException(breakpoint, address);
            }
        }
    }

    /**
     * Check if any of the breakpoints is triggered on given address.
     * Throws exception if breakpoint is triggered.
     * @param address Address to check
     */
    public void checkWrite(int address) throws WriteMemoryBreakpointException {
        if (ignoreNext != null && ignoreNext == address) {
            ignoreNext = null;
            return;
        }

        for (MemoryBreakpoint breakpoint : breakpoints) {
            if (breakpoint.checkWrite(address)) {
                ignoreNext = address;
                throw new WriteMemoryBreakpointException(breakpoint, address);
            }
        }
    }

    public void add(MemoryBreakpoint breakpoint) {
        this.breakpoints.add(breakpoint);
    }

    public void remove(MemoryBreakpoint breakpoint) {
        this.breakpoints.remove(breakpoint);
    }

    public void remove(int breakpointIndex) {
        this.breakpoints.remove(breakpointIndex);
    }


}


