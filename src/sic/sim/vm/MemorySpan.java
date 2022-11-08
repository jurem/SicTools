package sic.sim.vm;

public class MemorySpan {
    private int startAddress;
    private int spanLength;
    public MemorySpan() {
        this(-1, 0);
    }
    public MemorySpan(int startAddress, int spanLength) {
        this.startAddress = startAddress;
        this.spanLength = spanLength;
    }
    public int getStartAddress() {
        return startAddress;
    }
    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }
    public int getEndAddress() {
        return startAddress + spanLength;
    }
    public void setEndAddress(int endAddress) {
        spanLength = endAddress -  startAddress;
    }
    public int getSpanLength() {
        return spanLength;
    }
    public void setSpanLength(int spanLength) {
        this.spanLength = spanLength;
    }
    public void set(int startAddress, int spanLength) {
        this.startAddress = startAddress;
        this.spanLength = spanLength;
    }
    public boolean addressWithinSpan(int address) {
        return address >= startAddress && address < getEndAddress();
    }
    public void clear() {
        startAddress = -1;
        spanLength = 0;
    }
    @Override
    public String toString() {
        return "MemorySpan [startAddress=" + startAddress + ", spanLength=" + spanLength + "]";
    }
}
