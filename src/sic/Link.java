package sic;

import sic.link.Linker;
import sic.link.LinkerError;
import sic.link.Options;
import sic.link.section.Section;
import sic.link.utils.Writer;

import java.util.ArrayList;
import java.util.List;

/*
 * SIC/XE Linker
 *
 */
public class Link {

    private String output = null;

    public static void main(String[] args) {
        try {
            // get options
            Options options = new Options();
            int processedArgs = options.processFlags(args);

            // get the input files
            List<String> inputs = processInputs(args, processedArgs);

            Linker linker = new Linker(inputs, options);
            Section linkedSection = linker.link();

            Writer writer = new Writer(linkedSection, options);
            writer.write();

        } catch (LinkerError le) {
            System.err.println(le.getMessage());
        }
    }



    private static List<String> processInputs(String[] args, int start) {
        List<String> inputs = new ArrayList<>();

        for (int i=start; i<args.length; i++)
            inputs.add(args[i]);

        return inputs;
    }

}
