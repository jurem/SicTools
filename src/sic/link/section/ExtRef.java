package sic.link.section;

import sic.link.LinkerError;
import sic.link.visitors.SectionVisitor;

/**
 * External reference
 */
public class ExtRef {
    private String name;

    private Location location;

    public ExtRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ExtRef{" +
                "name='" + name + '\'' +
                '}';
    }

    public void accept(SectionVisitor visitor) throws LinkerError {
        visitor.visit(this);
    }
}
