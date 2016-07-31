package sic.link.section;

import sic.link.LinkerError;
import sic.link.visitors.SectionVisitor;

/*
 * External definition
 */
public class ExtDef {

    private String name;
    private long address;
    private long csAddress;
    private boolean delete = false;

    private Location location;

    public ExtDef(String name, long address) {
        this.name = name;
        this.address = address;
        this.csAddress = 0;
    }

    public ExtDef(String name, long address, long csAddress) {
        this.name = name;
        this.address = address;
        this.csAddress = csAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public long getCsAddress() {
        return csAddress;
    }

    public void setCsAddress(long csAddress) {
        this.csAddress = csAddress;
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
        return "ExtDef{" +
                "name='" + name + '\'' +
                ", address=" + address +
                '}';
    }

    public void accept(SectionVisitor visitor) throws LinkerError {
        visitor.visit(this);
    }

}
