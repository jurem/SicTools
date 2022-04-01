package sic.link.visitors;

import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.*;

import java.util.Map;

/*
 * Second pass
 *  changes Text records according to Modification Records
 */
public class SecondPassVisitor extends SectionVisitor {

    private static final String PHASE = "second pass";

    private Map<String, ExtDef> esTable;

    private Section currSection = null;
    private Options options;
    private String progname;


    public SecondPassVisitor(String progname, Map<String, ExtDef> esTable, Options options) {
        this.progname = progname;
        this.esTable = esTable;
        this.options = options;
    }

    @Override
    public void visit(Section section) throws LinkerError {

        currSection = section;

        //visit all mRecords
        if (section.getMRecords() != null) {
            for (MRecord mRecord : section.getMRecords())
                mRecord.accept(this);
        }
    }

    @Override
    public void visit(MRecord mRecord) throws LinkerError {
        if (mRecord.getSymbol() != null && !mRecord.getSymbol().equals(progname)) {

            ExtDef symbol = esTable.get(mRecord.getSymbol());
            if (symbol == null) {
                if (options.isForce()) {
                    if (options.isVerbose()) System.out.println(mRecord.getSymbol() + " is not defined in any section, allowing because -force option is set");
                    return;
                } else {
                    throw new LinkerError(PHASE, mRecord.getSymbol() + " is not defined in any section ", mRecord.getLocation());
                }
            }

            long fixAddressStart = mRecord.getStart() + currSection.getStart();
            long fixAddressEnd = fixAddressStart + mRecord.getLength() / 2;

            // find the Trecord that has to be fixed
            TRecord fixRecord = null;
			TRecord fixRecordEnd = null;
			int found = 0;
            if (currSection.getTRecords() != null) {
                for (TRecord tRecord : currSection.getTRecords()) {
                    if (tRecord.contains(fixAddressStart)) {
						found++;
                        fixRecord = tRecord;
						if (tRecord.contains(fixAddressEnd) || found == 2)
							break;

                    }
                    if (tRecord.contains(fixAddressEnd)) {
						found++;
                        fixRecordEnd = tRecord;
						if (found == 2)
							break;
                    }
                }
            }

            // throw an error if record was not found
            if (fixRecord == null)
                throw new LinkerError(PHASE, "Address " + fixAddressStart + " is not present in any T Record", mRecord.getLocation());

            // each byte is 2 chars
            int start = (int)(fixAddressStart - fixRecord.getStartAddr()) * 2; // start of the addressed word

            start = start + 6 - mRecord.getLength(); // last mRecord.getLength() halfbytes of the adressed word
            int end = start + mRecord.getLength();

            String text = fixRecord.getText();
			int recordLength = text.length();
			if (fixRecordEnd != null && fixRecord != fixRecordEnd) {
				text += fixRecordEnd.getText();
			}

            String fixBytes = text.substring(start, end);

            // add the address of extdef's section
            long corrected =  Integer.decode("0x" + fixBytes) + symbol.getCsAddress();

            // add or substract the symbol address
            if (mRecord.isPositive())
                corrected += symbol.getAddress();
            else
                corrected -= symbol.getAddress(); // rarely needed, example in Leland Beck's "System Software", Figure 2.15 line 190

            String correctedString = String.format("%0" + mRecord.getLength() + "X",corrected);

            text = text.substring(0,start) + correctedString + text.substring(end);
            fixRecord.setText(text.substring(0,recordLength));
			if (fixRecordEnd != null && fixRecord != fixRecordEnd) {
				text = text.substring(recordLength);
				fixRecordEnd.setText(text);
			}

            if (options.isVerbose()) System.out.println("fixing " + mRecord.getLength() + " half-bytes from " + fixBytes + " to " + correctedString + "   symbol=" + symbol.getName());

            // remove symbol from M record
            mRecord.setSymbol(progname);
            mRecord.setStart(mRecord.getStart() + currSection.getStart());

        }
        // else this is a regular M record - not for external symbols, ignore

    }
}
