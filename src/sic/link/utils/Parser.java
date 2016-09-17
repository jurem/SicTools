package sic.link.utils;

import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Parser for .obj files
 * "input" is the absolute path to the .obj file
 * parse() returns a list of Sections in the input file
 */
public class Parser {
    private static final String PHASE = "parser";

    private String input;
    private int row;
    private Options options;

    public Parser(String input, Options options) {
        this.input = input;
        this.options = options;
    }

    public List<Section> parse() throws LinkerError {

        List<Section> sects = new ArrayList<>();

        try {
            Section currSect = null;

            BufferedReader reader = new BufferedReader(new FileReader(input));

            char c = (char) reader.read();
            row = 0;

            while (c != (char)-1 && reader.ready()) {
                switch (c) {
                    case 'H':

                        // add current section to the list
                        if (currSect != null) {
                            sects.add(currSect);
                        }

                        // start a new section
                        String h = reader.readLine();
                        row++;
                        if (options.isVerbose())
                            System.out.println("reading H record: " + h);

                        if (h.length() == 18) {
                            try {

                                String name = h.substring(0, 6).replace(" ", "");
                                long start = Long.decode("0x" + h.substring(6, 12));
                                long length = Long.decode("0x" + h.substring(12, 18));

                                if (start != 0)
                                    throw new LinkerError(PHASE, "The section " + name + " is not relative.", new Location(input, row));

                                currSect = new Section(name, start, length);
                                currSect.setLocation(new Location(input, row));

                            } catch (NumberFormatException nfe) {
                                throw new LinkerError(PHASE, "Wrong H record format",  new Location(input, row));
                            }

                        } else {
                            throw new LinkerError(PHASE, "H record has incorrect length", new Location(input, row));
                        }

                        c = (char) reader.read();
                        break;
                    case 'E':
                        if (currSect == null) throw new LinkerError(PHASE, "Missing H record", new Location(input, row));
                        String e = reader.readLine();
                        row++;
                        if (options.isVerbose())
                            System.out.println("reading E record: " + e);

                        long startAddr = Long.decode("0x" + e);

                        ERecord eRecord = new ERecord(startAddr);
                        eRecord.setLocation(new Location(input, row));
                        currSect.setERecord(eRecord);

                        // add section to the list
                        sects.add(currSect);

                        currSect = null;

                        c = (char) reader.read();
                        break;
                    case 'T':
                        if (currSect == null) {
                            if (currSect == null) throw new LinkerError(PHASE, "Missing H record", new Location(input, row));
                        }

                        String t = reader.readLine();
                        row++;
                        if (options.isVerbose())
                            System.out.println("reading T record: " + t);


                        long tStart = Long.decode("0x" + t.substring(0,6));
                        long tLength = Long.decode("0x" + t.substring(6,8));
                        String text = t.substring(8);

                        TRecord tRecord = new TRecord(tStart, tLength, text);
                        tRecord.setLocation(new Location(input, row));
                        currSect.addTRecord(new TRecord(tStart, tLength, text));

                        // read next char
                        c = (char) reader.read();
                        break;
                    case 'M':
                        if (currSect == null) throw new LinkerError(PHASE, "Missing H record", new Location(input, row));

                        String m = reader.readLine();
                        row++;
                        if (options.isVerbose())
                            System.out.println("reading M record: " + m);

                        long mStart = Long.decode("0x" + m.substring(0,6));
                        long mLength = Long.decode("0x" + m.substring(6,8)); // number of hex chars, not bytes

                        boolean direction = true;
                        String symbol = null;

                        // if adding/substracting an ext symbol
                        if (m.length() > 8) {
                            direction = m.charAt(8) == '+';
                            symbol = m.substring(9).replace(" ", "");
                        } else {
                            direction = true;
                            symbol = currSect.getName();
                        }

                        MRecord mRecord = new MRecord(mStart, (int) mLength, direction, symbol);
                        mRecord.setLocation(new Location(input, row));
                        currSect.addMRecord(mRecord);

                        // read next char
                        c = (char) reader.read();
                        break;
                    case 'R':
                        if (currSect == null) throw new LinkerError(PHASE, "Missing H record", new Location(input, row));

                        String r = reader.readLine();
                        row++;

                        if (options.isVerbose())
                            System.out.println("reading R record: " + r);

                        for (int i=0; i<r.length(); i+=6) {
                            String sym = r.substring(i, i+6).replace(" ", "");

                            ExtRef extRef = new ExtRef(sym);
                            extRef.setLocation(new Location(input, row));
                            currSect.addExtRef(extRef);
                        }

                        c = (char) reader.read();
                        break;
                    case 'D':
                        if (currSect == null) throw new LinkerError(PHASE, "Missing H record", new Location(input, row));

                        String d = reader.readLine();
                        row++;

                        if (options.isVerbose())
                            System.out.println("reading D record: " + d);

                        //TODO: check if there should be spaces between symbols

                        for (int i=0; i<d.length()-1; i+=12) {
                            if (d.charAt(i) == ' ') {
                                // jump over the space
                                i++;
                            }
                            String sym = d.substring(i, i+6).replace(" ", "");
                            sym = sym.replace(" ", ""); // remove whitespace in variable name

                            long symAddr = Long.decode("0x" + d.substring(i+6, i+12));

                            ExtDef extDef = new ExtDef(sym, symAddr);
                            extDef.setLocation(new Location(input, row));
                            currSect.addExtDef(extDef);
                        }
                        c = (char) reader.read();
                        break;
                    case '\n':
                        c = (char) reader.read();
                        row++;
                        break;

                    default:
                        throw new LinkerError(PHASE, "Unexpected character '" + c + "' while reading object file", new Location(input, row));
                }
            }

            // add the last section if it hasn't been closed by ERecord already
            if (currSect != null)
                sects.add(currSect);

            return sects;

        } catch (FileNotFoundException e) {
            throw new LinkerError(PHASE, "File not found" + input);
        } catch (IOException e) {
            throw new LinkerError(PHASE, "IO exception while reading the file " + input + ".");
        }
    }

}
