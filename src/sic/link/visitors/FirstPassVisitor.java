package sic.link.visitors;

import sic.link.LinkerError;
import sic.link.section.*;

import java.util.*;

/*
 * First pass visitor
 *  it records each Section in the CStable and each ExtDef in the EStable,
 *  changes the section addresses,
 *  changes symbol definition addresses
 *  changes text record start addresses
 *
 *  note: in the book, TextRecord changing is done in second pass, in my implementation it was easier here
 */
public class FirstPassVisitor extends SectionVisitor {

    // tables
    private Map<String, Section> csTable; // just to make sure we get each section once
    public Map<String, ExtDef> esTable;   // will be used in the second pass

    private long csAddr;

    public FirstPassVisitor(Map<String, ExtDef> esTable) {
        this.esTable = esTable;
        csTable = new HashMap<>();

        csAddr = 0;
    }

    @Override
    public void visit(Section section) throws LinkerError {
        String name = section.getName();

        // set the section start address0
        section.setStart(csAddr);

        // fill the CStable
        if (csTable.get(name) == null) {
            csTable.put(name, section);
        } else {
            throw new LinkerError("Duplicated section name: " + name + " at "
                    + section.getLocation() + " and " + csTable.get(name).getLocation());
        }

        if (section.getExtDefs() != null) {
            for (ExtDef extDef : section.getExtDefs())
                extDef.accept(this);
        }

        if (section.gettRecords() != null) {

            // sort TRecords by address (they might not be in order)
            Collections.sort(section.gettRecords(), new Comparator<TRecord>() {
                @Override
                public int compare(TRecord o1, TRecord o2) {

                    if (o1.getStartAddr() < o2.getStartAddr())
                        return -1;
                    else if (o1.getStartAddr() > o2.getStartAddr())
                        return 1;
                    else
                        return 0;
                }
            });

            // visit all T records
            for (TRecord tRecord : section.gettRecords())
                tRecord.accept(this);
        }

        // increment the section address
        csAddr += section.getLength();
    }

    @Override
    public void visit(ExtDef extDef) throws LinkerError {
        String name = extDef.getName();

        // set the section address
        extDef.setCsAddress(csAddr);

        // fill the EStable
        if (esTable.get(name) == null) {
            esTable.put(name, extDef);
        } else {
            throw new LinkerError("Duplicated external symbol definition '" +  name + "'", extDef.getLocation());
        }
    }

    @Override
    public void visit(TRecord tRecord) {
        // add section address to the startAddr
        long start = tRecord.getStartAddr();
        tRecord.setStartAddr(start + csAddr);
    }

}
