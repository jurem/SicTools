package sic.sim.breakpoints;

public class WriteDataBreakpointException extends DataBreakpointException {

    public WriteDataBreakpointException(DataBreakpoint breakpoint, int address) {
        super(breakpoint, address);
    }

}
