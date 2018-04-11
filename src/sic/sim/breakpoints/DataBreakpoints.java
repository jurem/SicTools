package sic.sim.breakpoints;

import java.util.ArrayList;

public class DataBreakpoints {
    private ArrayList<DataBreakpoint> breakpoints = new ArrayList<>();

    public DataBreakpoint triggered;

    public Integer ignoreNext;

    public DataBreakpoints() {

    }

    /**
     * Check if any of the breakpoints is triggered on given address
     * Throws exception if breakpoint is triggered.
     * @param address Address to check
     */
    public void checkRead(int address) throws ReadDataBreakpointException {
        if (ignoreNext != null && ignoreNext == address) {
            ignoreNext = null;
            return;
        }

        for (DataBreakpoint breakpoint : breakpoints) {
            if (breakpoint.checkRead(address)) {
                ignoreNext = address;
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
        if (ignoreNext != null && ignoreNext == address) {
            ignoreNext = null;
            return;
        }

        for (DataBreakpoint breakpoint : breakpoints) {
            if (breakpoint.checkWrite(address)) {
                ignoreNext = address;
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


}


