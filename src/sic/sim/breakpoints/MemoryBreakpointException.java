package sic.sim.breakpoints;

public abstract class MemoryBreakpointException extends Exception {

    private int address;
    private MemoryBreakpoint breakpoint;

    public MemoryBreakpointException(MemoryBreakpoint breakpoint, int address) {
        this.address = address;
        this.breakpoint = breakpoint;
    }

    public int getAddress() {
        return address;
    }

    public MemoryBreakpoint getBreakpoint() {
        return breakpoint;
    }
}
