package sic.link.visitors;

import sic.link.LinkerError;
import sic.link.section.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    private static final String PHASE = "first pass";

    // tables
    public Map<String, Section> csTable; // map list of all sections
    public Map<String, ExtDef> esTable;   // map of all external symbols

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
            // add a new extdef with section name -
            // 'section names are automatically considered to be external symbols'
            esTable.put(name, new ExtDef(name, 0, section.getStart()));
        } else {
            throw new LinkerError(PHASE, "Duplicated section name: " + name + " at "
                    + section.getLocation() + " and " + csTable.get(name).getLocation());
        }

        if (section.getExtDefs() != null) {
            for (ExtDef extDef : section.getExtDefs())
                extDef.accept(this);
        }

        if (section.getTRecords() != null) {

            // sort TRecords by address (they might not be in order)
            Collections.sort(section.getTRecords(), (t1, t2) -> {

                if (t1.getStartAddr() < t2.getStartAddr())
                    return -1;
                else if (t1.getStartAddr() > t2.getStartAddr())
                    return 1;
                else
                    return 0;
            });

            // visit all T records
            for (TRecord tRecord : section.getTRecords())
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
            throw new LinkerError(PHASE, "Duplicated external symbol definition '" +  name + "'", extDef.getLocation());
        }
    }

    @Override
    public void visit(TRecord tRecord) {
        // add section address to the startAddr
        long start = tRecord.getStartAddr();
        tRecord.setStartAddr(start + csAddr);
    }

}
