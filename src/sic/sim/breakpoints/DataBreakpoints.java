package sic.sim.breakpoints;

import java.util.ArrayList;

public class DataBreakpoints {
    private ArrayList<DataBreakpoint> breakpoints = new ArrayList<>();

    public DataBreakpoint triggered;

    public int ignoreNextAmount = 0;
    // For handling multiple
    private int requestSize = 1;
    private int requestLeft = 0;

    public DataBreakpoints() {

    }

    /**
     * Check if any of the breakpoints is triggered on given address
     * Throws exception if breakpoint is triggered.
     * @param address Address to check
     */
    public void checkRead(int address) throws ReadDataBreakpointException {
        requestReduce();
        if (shouldIgnore()) return;

        for (DataBreakpoint breakpoint : breakpoints) {
            if (breakpoint.checkRead(address)) {
                ignoreNextAmount = requestSize;
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
        requestReduce();
        if (shouldIgnore()) return;

        for (DataBreakpoint breakpoint : breakpoints) {
            if (breakpoint.checkWrite(address)) {
                ignoreNextAmount = requestSize;
                throw new WriteDataBreakpointException(breakpoint, address);
            }
        }
    }

    private boolean shouldIgnore() {
        if (this.ignoreNextAmount == 0) {
            return false;
        }
        this.ignoreNextAmount--;
        return true;
    }

    private void requestReduce() {
        if (requestLeft > 0) {
            requestLeft--;
        } else {
            requestSize = 1;
        }
    }

    public void requestMultiple(int size) {
        requestSize = size;
        requestLeft = size;
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


