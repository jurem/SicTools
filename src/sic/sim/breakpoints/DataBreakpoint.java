package sic.sim.breakpoints;

import sic.common.Conversion;

import java.security.InvalidParameterException;

public class DataBreakpoint {

    private int from;
    private int to;

    /**
     * On what kind of memory access should the breakpoint be triggered?
     */
    private MemoryAccess access;

    public enum MemoryAccess {
        WRITE, READ, BOTH, NONE
    }

    private boolean enabled = true;

    // ----------------------
    // |    Constructor     |
    // ----------------------
    public DataBreakpoint(int from, int to) {
        if (from > to) {
            throw new InvalidParameterException("Range should be from lower address to higher address!");
        }

        this.from = from;
        this.to = to;
    }

    public DataBreakpoint(int from, int to, MemoryAccess access) {
        this(from, to);
        this.access = access;
    }

    public DataBreakpoint(int from, int to, boolean read, boolean write) {
        this(from, to);
        this.access = memoryAccessFromBool(read, write);
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
        return from <= address && address <= to;
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


    // ---
    // Helper
    public static MemoryAccess memoryAccessFromBool(boolean read, boolean write) {
        if (read && write) return MemoryAccess.BOTH;
        else if (read) return MemoryAccess.READ;
        else if (write) return MemoryAccess.WRITE;
        else return MemoryAccess.NONE;
    }

    public Object[] toTable() {
        Object[] fields = new Object[5];
        fields[0] = Conversion.addrToHex(from);
        fields[1] = Conversion.addrToHex(to);
        fields[2] = getRead();
        fields[3] = getWrite();
        fields[4] = isEnabled();
        return fields;
    }

}
