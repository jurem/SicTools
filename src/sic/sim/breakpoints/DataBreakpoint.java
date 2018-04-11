package sic.sim.breakpoints;

import java.security.InvalidParameterException;

public class DataBreakpoint {

    private int from;
    private int to;

    /**
     * On what kind of memory access should the breakpoint be triggered?
     */
    private MemoryAccess access;

    public enum MemoryAccess {
        WRITE, READ, BOTH
    }

    private boolean enabled = true;

    // ----------------------
    // |    Constructor     |
    // ----------------------
    public DataBreakpoint(int from, int to, MemoryAccess access) {
        if (from > to) {
            throw new InvalidParameterException("Range should be from lower address to higher address!");
        }

        this.from = from;
        this.to = to;
        this.access = access;
    }

    // ----------------------
    // |  Getters & Setters |
    // ----------------------
    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public MemoryAccess getAccess() {
        return access;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccess(MemoryAccess access) {
        this.access = access;
    }

    public void setRange(int from, int to) {
        if (from > to) {
            throw new InvalidParameterException("Range should be from lower address to higher address!");
        }

        this.from = from;
        this.to = to;
    }

    public boolean getRead() {
        return access == MemoryAccess.READ || access == MemoryAccess.BOTH;
    }

    public boolean getWrite() {
        return access == MemoryAccess.WRITE || access == MemoryAccess.BOTH;
    }

    // ----------------------
    // |      Methods       |
    // ----------------------

    /**
     * Checks if the address is inside the breakpoint's range
     * @param address Address to check
     */
    public boolean addressInside(int address) {
        return from >= address && address <= to;
    }

    /**
     * Checks if the breakpoint should be triggered
     * for reading on given address
     * @param address Address to check
     */
    public boolean checkRead(int address) {
        return addressInside(address) && this.enabled && getRead();
    }

    /**
     * Checks if the breakpoint should be triggered
     * for writing on given address
     * @param address Address to check
     */
    public boolean checkWrite(int address) {
        return addressInside(address) && this.enabled && getWrite();
    }

}
