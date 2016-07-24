package sic.link.visitors;

import org.omg.CORBA.TRANSACTION_REQUIRED;
import sic.link.LinkerError;
import sic.link.section.*;

import java.util.List;

/*
 * abstract Section visitor - just visits all the sections
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

        if (section.gettRecords() != null)
            for (TRecord tRecord : section.gettRecords())
                tRecord.accept(this);

        if (section.getmRecords() != null)
            for (MRecord mRecord : section.getmRecords())
                mRecord.accept(this);
    }

    public void visit(ExtDef extDef) throws LinkerError {}

    public void visit(ExtRef extRef) {}

    public void visit(TRecord tRecord) {}

    public void visit(MRecord mRecord) throws LinkerError {}

}
