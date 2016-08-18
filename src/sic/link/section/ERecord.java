package sic.link.section;

/**
 * Created by nejc on 10/07/16.
 */
public class ERecord {
    private long startAddr;

    private Location location;

    public ERecord(long startAddr) {
        this.startAddr = startAddr;
    }

    public long getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(long startAddr) {
        this.startAddr = startAddr;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ERecord{" +
                "startAddr=" + startAddr +
                '}';
    }
}
