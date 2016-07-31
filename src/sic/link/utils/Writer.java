package sic.link.utils;

import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/*
 * Writes a section to a specified .obj file
 *
 * if skipExt is set to true, it will skip any external symbols and M records referencing them
 */
public class Writer {
    private static final String PHASE = "writer";

    private Options options;
    private Section section;

    private boolean skipExt = true;

    public Writer (Section section, Options options) {
        this.options = options;
        this.section = section;
    }

    public void write() throws LinkerError {
        PrintWriter writer;

        if (options.getOutputPath() == null) {
            options.setOutputName(section.getName() + "_ln.obj");
            options.setOutputPath(section.getName() + "_ln.obj");
        }

        try {
            writer = new PrintWriter(options.getOutputPath());
        } catch (FileNotFoundException e) {
            throw new LinkerError(PHASE, "Unable to write to " + options.getOutputPath());
        }

        if (options.isVerbose())
            System.out.println("writing the result to " + options.getOutputPath());

        if (section.getName().length() > 6)
            section.setName(section.getName().substring(0,6));

        // header
        writer.print("H");
        writer.printf("%-6s", section.getName());
        writer.printf("%06X", section.getStart());
        writer.printf("%06X", section.getLength());
        writer.println();

        //text records
        if (section.gettRecords() != null)
            for (TRecord t : section.gettRecords()) {
                writer.print("T");
                writer.printf("%06X", t.getStartAddr());
                writer.printf("%02X", t.getLength());
                writer.print(t.getText());
                writer.println();
            }

        //modification records
        if (section.getmRecords() != null)
            for (MRecord m : section.getmRecords()) {
                writer.print("M");
                writer.printf("%06X", m.getStart());
                writer.printf("%02X", m.getLength());


                if (m.getSymbol() != null && !skipExt) {
                    if (m.isPositive())
                        writer.print("+");
                    else
                        writer.print("-");

                    writer.print(m.getSymbol());
                }
                writer.println();
            }

        // if forced, we keep some of the references & definitions
        if (!skipExt) {
            if (section.getExtDefs() != null)
                for (ExtDef d : section.getExtDefs()){
                    writer.print("D");
                    writer.printf("%6s", d.getName());
                    writer.printf("%06X", d.getCsAddress() + d.getAddress()); // TODO: check this
                    writer.println(" ");
                }

            if (section.getExtRefs() != null)
                for (ExtRef r : section.getExtRefs()){
                    writer.print("D");
                    writer.printf("%6s", r.getName());
                    writer.println();
                }
        }

        if (section.geteRecord() != null) {
            writer.print("E");
            writer.printf("%06X", section.geteRecord().getStartAddr());
            writer.println();
        }

        writer.close();
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public boolean isSkipExt() {
        return skipExt;
    }

    public void setSkipExt(boolean skipExt) {
        this.skipExt = skipExt;
    }
}
