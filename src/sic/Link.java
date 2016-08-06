package sic;

import sic.link.*;
import sic.link.section.Section;
import sic.link.utils.Writer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/*
 * SIC/XE Linker
 *
 */
public class Link {

    public static void main(String[] args) {
        try {
            // get options
            Options options = new Options();
            int processedArgs = options.processFlags(args);

            // get the input files
            List<String> inputs = processInputs(args, processedArgs);

            if (options.isGraphical())
                LinkerGui.gui(options, inputs, new LinkerGui.GuiLinkListener());
            else
                link(options, inputs);

        } catch (LinkerError le) {
            System.err.println(le.getMessage());
        }
    }

    public static File link(Options options, List<String> inputs) throws LinkerError {
        Linker linker = new Linker(inputs, options);
        Section linkedSection = linker.link();

        Writer writer = new Writer(linkedSection, options);
        return writer.write();
    }

    private static List<String> processInputs(String[] args, int start) {
        List<String> inputs = new ArrayList<>();

        for (int i=start; i<args.length; i++)
            inputs.add(args[i]);

        return inputs;
    }

}
