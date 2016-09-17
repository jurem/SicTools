package sic.link.section;

/*
 * describes the location of a record
 */
public class Location {
    private String filename;
    private int row;

    public Location(String filename) {
        this.filename = filename;
        this.row = -1;
    }

    public Location(String filename, int row) {
        this.filename = filename;
        this.row = row;
    }


    @Override
    public String toString() {
        if (row < 0)
            return filename;
        else
            return filename + ", row " + row;
    }
}
