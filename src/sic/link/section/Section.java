package sic.link.section;

import sic.link.LinkerError;
import sic.link.visitors.SectionVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Section
 */
public class Section {

    // section data
    // --------------------------
    private String name;
    private long start;
    private long length;
    private List<TRecord> tRecords;
    private List<MRecord> mRecords;
    private List<ExtRef> extRefs;
    private List<ExtDef> extDefs;
    private ERecord eRecord;

    // metadata for displaying error messages (filename and row)
    // --------------------------
    private Location location;

    // constructors
    // --------------------------
    public Section(String name, long start, long length, List<TRecord> tRecords, List<MRecord> mRecords,
                   List<ExtRef> extRefs, List<ExtDef> extDefs, ERecord eRecord) {
        this.name = name;
        this.start = start;
        this.length = length;
        this.tRecords = tRecords;
        this.mRecords = mRecords;
        this.extRefs = extRefs;
        this.extDefs = extDefs;
        this.eRecord = eRecord;
    }

    public Section(String name, long start, long length) {
        this.name = name;
        this.start = start;
        this.length = length;
        this.tRecords = new ArrayList<>();
        this.mRecords = new ArrayList<>();
        this.extRefs = new ArrayList<>();
        this.extDefs = new ArrayList<>();
        this.eRecord = null;
    }

    // getters and setters
    // --------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public List<TRecord> getTRecords() {
        return tRecords;
    }

    public void setTRecords(List<TRecord> tRecords) {
        this.tRecords = tRecords;
    }

    public void addTRecord(TRecord tRecord) {
        if (this.tRecords == null)
            this.tRecords = new ArrayList<>();

        this.tRecords.add(tRecord);
    }

    public List<MRecord> getMRecords() {
        return mRecords;
    }

    public void setMRecords(List<MRecord> mRecords) {
        this.mRecords = mRecords;
    }

    public void addMRecord(MRecord mRecord) {
        if (this.mRecords == null)
            this.mRecords = new ArrayList<>();

        this.mRecords.add(mRecord);
    }

    public List<ExtRef> getExtRefs() {
        return extRefs;
    }

    public void setExtRefs(List<ExtRef> extRefs) {
        this.extRefs = extRefs;
    }

    public List<ExtDef> getExtDefs() {
        return extDefs;
    }

    public void addExtDef(ExtDef extDef) {
        if (this.extDefs == null)
            this.extDefs = new ArrayList<>();

        this.extDefs.add(extDef);
    }

    public void setExtDefs(List<ExtDef> extDefs) {
        this.extDefs = extDefs;
    }

    public ERecord getERecord() {
        return eRecord;
    }

    public void addExtRef(ExtRef extRef) {
        if (this.extRefs == null)
            this.extRefs = new ArrayList<>();

        this.extRefs.add(extRef);
    }

    public void setERecord(ERecord eRecord) {
        this.eRecord = eRecord;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {

        String nl = System.lineSeparator();

        StringBuilder builder = new StringBuilder();

        builder.append("Section {" + nl);
        builder.append(" name='" + this.name + "', start=" + this.start + ", length=" + this.length + nl);
        builder.append(" T:" + nl);
        if (this.tRecords != null) {
            for (TRecord tRecord : this.tRecords)
                builder.append("\t" + tRecord.toString()+nl);
        } else {
            builder.append("\t-" + nl);
        }

        builder.append(" D:" + nl);
        if (this.extDefs != null && this.extDefs.size() > 0) {
            for (ExtDef extDef : this.extDefs)
                builder.append("\t" + extDef.toString()+nl);
        } else {
            builder.append("\t-" + nl);
        }

        builder.append(" R:" + nl);
        if (this.extRefs != null && this.extRefs.size() > 0) {
            for (ExtRef extRef : this.extRefs)
                builder.append("\t" + extRef.toString()+nl);
        } else {
            builder.append("\t-" + nl);
        }

        builder.append(" M:" + nl);
        if (this.mRecords != null && this.mRecords.size() > 0) {
            for (MRecord mRecord : this.mRecords)
                builder.append("\t" + mRecord.toString()+nl);
        } else {
            builder.append("\t-" + nl);
        }

        if (eRecord != null)
            builder.append(" E: " + eRecord.toString() + nl);
        else
            builder.append(" No E record" + nl);

        builder.append("}");

        return builder.toString();
    }

    public void accept(SectionVisitor visitor) throws LinkerError {
        visitor.visit(this);
    }
}
