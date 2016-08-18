package sic.link.visitors;

import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.*;

import java.util.ListIterator;
import java.util.Map;

/*
 * Second pass
 *  changes Text records according to Modification Records
 */
public class SecondPassVisitor extends SectionVisitor {

    private static final String PHASE = "second pass";

    private Map<String, ExtDef> esTable;
    private Map<String, Section> csTable;

    private Section currSection = null;
    private Options options;


    public SecondPassVisitor(Map<String, ExtDef> esTable, Map<String, Section> csTable, Options options) {
        this.esTable = esTable;
        this.csTable = csTable;
        this.options = options;
    }

    @Override
    public void visit(Section section) throws LinkerError {

        currSection = section;

        //visit all mRecords
        if (section.getmRecords() != null) {
            ListIterator<MRecord> iter = section.getmRecords().listIterator();

            while (iter.hasNext()) {
                MRecord mRecord = iter.next();
                mRecord.accept(this);

                // remove the m record if it is marked to be deleted
                if (mRecord.getDelete())
                    iter.remove();
            }

        }

    }

    @Override
    public void visit(MRecord mRecord) throws LinkerError {
        if (mRecord.getSymbol() != null) {

            ExtDef symbol = esTable.get(mRecord.getSymbol());
            if (symbol == null) {
                if (options.isForce()) {
                    if (options.isVerbose()) System.out.println(mRecord.getSymbol() + " is not defined in any section, allowing because -force option is set");
                    return;
                } else {
                    throw new LinkerError(PHASE, mRecord.getSymbol() + " is not defined in any section ", mRecord.getLocation());
                }
            }

            long fixAddress = mRecord.getStart() + currSection.getStart();

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
                throw new LinkerError(PHASE, "Address " + fixAddress + " is not present in any T Record", mRecord.getLocation());

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
                corrected -= symbol.getAddress(); // rarely needed, example in Leland Beck's System Software, Figure 2.15 line 190

            String correctedString = String.format("%0" + mRecord.getLength() + "X",corrected);
            text = text.substring(0,start) + correctedString + text.substring(start + mRecord.getLength());
            fixRecord.setText(text);

            if (options.isVerbose()) System.out.println("fixing " + mRecord.getLength() + " half-bytes from " + fixBytes + " to " + correctedString + "   symbol=" + symbol.getName());

            mRecord.setDelete(true);

        }
        // else this is a regular M record - not for external symbols, ignore


    }
}
