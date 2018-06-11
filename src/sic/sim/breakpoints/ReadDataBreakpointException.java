package sic.sim.breakpoints;

public class ReadDataBreakpointException extends DataBreakpointException {

    public ReadDataBreakpointException(DataBreakpoint breakpoint, int address) {
        super(breakpoint, address);
    }

}
