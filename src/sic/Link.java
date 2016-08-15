package sic;

import sic.link.*;
import sic.link.ui.LinkListener;
import sic.link.ui.LinkerCli;
import sic.link.ui.LinkerGui;
import sic.link.section.Section;
import sic.link.section.Sections;
import sic.link.ui.SectionEditListener;
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
                LinkerGui.gui(options, inputs, new LinkListener() {
                    @Override
                    public void onLinked(File f, String message) {
                        if (f != null) {
                            LinkerGui.showSuccess(f.getAbsolutePath());
                        } else {
                            LinkerGui.showError(message);
                        }
                    }
                });
            else
                link(options, inputs);

        } catch (LinkerError le) {
            System.err.println(le.getMessage());
        }
    }

    public static File link(Options options, List<String> inputs) throws LinkerError {
        Linker linker = new Linker(inputs, options);

        if (options.isInteractive()) {
            Sections sections = linker.parse();

            sections = LinkerCli.sectionEdit(sections);

            Section linkedSection = linker.passAndCombine(sections);

            Writer writer = new Writer(linkedSection, options);
            return writer.write();

        } else {
            Section linkedSection = linker.link();

            Writer writer = new Writer(linkedSection, options);
            return writer.write();
        }

    }

    private static List<String> processInputs(String[] args, int start) {
        List<String> inputs = new ArrayList<>();

        for (int i=start; i<args.length; i++)
            inputs.add(args[i]);

        return inputs;
    }

}
