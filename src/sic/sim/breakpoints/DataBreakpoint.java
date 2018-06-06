package sic.sim.breakpoints;

import sic.common.Conversion;

import java.security.InvalidParameterException;

public class DataBreakpoint {

    private int from;
    private int to;

    private boolean read = false; // trigger on read access
    private boolean write = false; // trigger on write access

    private boolean enabled = true;

    // ----------------------
    // |    Constructor     |
    // ----------------------
    private DataBreakpoint(int from, int to) {
        if (from > to) {
            throw new InvalidParameterException("Range should be from lower address to higher address!");
        }

        this.from = from;
        this.to = to;
    }

    public DataBreakpoint(int from, int to, boolean read, boolean write) {
        this(from, to);
        this.read = read;
        this.write = write;
    }

    public DataBreakpoint(int from, int to, boolean read, boolean write, boolean enabled) {
        this(from, to, read, write);
        this.enabled = enabled;
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public void setRange(int from, int to) {
        if (from > to) {
            throw new InvalidParameterException("Range should be from lower address to higher address!");
        }

        this.from = from;
        this.to = to;
    }

    public boolean getRead() {
        return this.read;
    }

    public boolean getWrite() {
        return this.write;
    }

    public void toggleRead() {
        this.read = !this.read;
    }

    public void toggleWrite() {
        this.write = !this.write;
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
