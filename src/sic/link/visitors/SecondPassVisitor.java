package sic.link.visitors;

import sic.link.LinkerError;
import sic.link.section.*;

import java.util.Map;

/*
 * Second pass
 *  changes Text records according to Modification Records
 */
public class SecondPassVisitor extends SectionVisitor {

    private Map<String, ExtDef> esTable;

    private Section currSection = null;


    public SecondPassVisitor(Map<String, ExtDef> esTable) {
        this.esTable = esTable;
    }

    @Override
    public void visit(Section section) throws LinkerError {

        currSection = section;

        //visit all mRecords
        if (section.getmRecords() != null) {
            for (MRecord mRecord : section.getmRecords())
                mRecord.accept(this);
        }

    }

    @Override
    public void visit(MRecord mRecord) throws LinkerError {
        if (mRecord.getSymbol() != null) {

            ExtDef symbol = esTable.get(mRecord.getSymbol());
            if (symbol == null) {
                // TODO add an option flag that can deal with this
                throw new LinkerError(mRecord.getSymbol() + " is not defined in any section ", mRecord.getLocation() );
            }

            long fixAddress = mRecord.getStart();

            // find the Trecord that has to be fixed
            TRecord fixRecord = null;
            if (currSection.gettRecords() != null) {
                for (TRecord tRecord : currSection.gettRecords()) {
                    if (tRecord.contains(fixAddress)) {
                        fixRecord = tRecord;
                        break;
                    }
                }
            }

            // throw an error if record was not found
            if (fixRecord == null)
                throw new LinkerError("Address " + fixAddress + " is not present in any T Record", mRecord.getLocation());

            // each byte is 2 chars
            int start = (int)(fixAddress - fixRecord.getStartAddr()) * 2;

            // if length is odd, we skip the first char
            if (mRecord.getLength() % 2 == 1)
                start++;

            String text = fixRecord.getText();

            String fixBytes = text.substring(start, start + mRecord.getLength());

            // add the address of extdef's section
            long corrected =  Integer.decode("0x" + fixBytes) + symbol.getCsAddress();

            // add or substract the symbol address
            if (mRecord.isPositive())
                corrected += symbol.getAddress();
            else
                corrected -= symbol.getAddress(); // TODO: when is this needed?

            String correctedString = String.format("%0" + mRecord.getLength() + "X",corrected);
            text = text.substring(0,start) + correctedString + text.substring(start + mRecord.getLength());
            fixRecord.setText(text);

            mRecord.setDelete(true);

        } else {
            // this is a regular M record - not for external symbols
        }

    }
}
