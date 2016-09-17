package sic.link.section;

import sic.link.LinkerError;
import sic.link.visitors.SectionVisitor;

/**
 * Text record
 */
public class TRecord {

    private long startAddr;
    private long length;
    private String text;

    private Location location;

    public TRecord(long startAddr, long length, String text) {
        this.startAddr = startAddr;
        this.length = length;
        this.text = text;
    }

    public long getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(long startAddr) {
        this.startAddr = startAddr;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean contains(long addr) {
        return (addr >= startAddr && addr < startAddr+length);
    }

    @Override
    public String toString() {
        return "TRecord{" +
                "startAddr=" + startAddr +
                ", length=" + length +
                ", text='" + text + '\'' +
                '}';
    }

    public void accept(SectionVisitor visitor) throws LinkerError {
        visitor.visit(this);
    }
}
