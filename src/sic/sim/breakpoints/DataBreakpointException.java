package sic.sim.breakpoints;

public abstract class DataBreakpointException extends Exception {

    private int address;
    private DataBreakpoint breakpoint;

    public DataBreakpointException(DataBreakpoint breakpoint, int address) {
        this.address = address;
        this.breakpoint = breakpoint;
    }

    public int getAddress() {
        return address;
    }

    public DataBreakpoint getBreakpoint() {
        return breakpoint;
    }
}
