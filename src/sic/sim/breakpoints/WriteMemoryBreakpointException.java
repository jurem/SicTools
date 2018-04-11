package sic.sim.breakpoints;

public class WriteMemoryBreakpointException extends MemoryBreakpointException {

    public WriteMemoryBreakpointException(MemoryBreakpoint breakpoint, int address) {
        super(breakpoint, address);
    }

}
