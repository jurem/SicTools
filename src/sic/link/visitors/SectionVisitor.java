package sic.link.visitors;

import sic.link.LinkerError;
import sic.link.section.*;


/*
 * abstract Section visitor - visits all the records in all sections
 */
public abstract class SectionVisitor {

    public void visit(Sections sections) throws LinkerError {
        for (Section section : sections.getSections())
            section.accept(this);
    }

    public void visit(Section section) throws LinkerError {

        if (section.getExtDefs() != null)
            for (ExtDef extDef : section.getExtDefs())
                extDef.accept(this);

        if (section.getExtRefs() != null)
            for (ExtRef extRef : section.getExtRefs())
                extRef.accept(this);

        if (section.getTRecords() != null)
            for (TRecord tRecord : section.getTRecords())
                tRecord.accept(this);

        if (section.getMRecords() != null)
            for (MRecord mRecord : section.getMRecords())
                mRecord.accept(this);
    }

    public void visit(ExtDef extDef) throws LinkerError {}

    public void visit(ExtRef extRef) throws LinkerError{}

    public void visit(TRecord tRecord) throws LinkerError{}

    public void visit(MRecord mRecord) throws LinkerError {}

    public void visit(ERecord eRecord) throws LinkerError {}

}
