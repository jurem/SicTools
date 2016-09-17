package sic.link.section;

import sic.link.LinkerError;
import sic.link.visitors.SectionVisitor;

/**
 * Modification record
 */
public class MRecord {

    private long start;
    private int length;
    private boolean positive;
    private String symbol;
    private Location location;
    private boolean delete;

    public MRecord(long start, int length, boolean positive, String symbol) {
        this.start = start;
        this.length = length;
        this.positive = positive;
        this.symbol = symbol;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "MRecord{" +
                "start=" + start +
                ", length=" + length +
                ", positive=" + positive +
                ", symbol='" + symbol + '\'' +
                '}';
    }
    public void accept(SectionVisitor visitor) throws LinkerError {
        visitor.visit(this);
    }
}
