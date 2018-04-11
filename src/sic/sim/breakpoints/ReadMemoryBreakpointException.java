package sic.sim.breakpoints;

public class ReadMemoryBreakpointException extends MemoryBreakpointException {

    public ReadMemoryBreakpointException(MemoryBreakpoint breakpoint, int address) {
        super(breakpoint, address);
    }

}
