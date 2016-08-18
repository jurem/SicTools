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
            List<String> inputs = new ArrayList<>();

            for (int i=processedArgs; i<args.length; i++)
                inputs.add(args[i]);

            if (options.isGraphical()) {
                LinkerGui linkerGui = new LinkerGui(options, inputs, new LinkListener() {
                    @Override
                    public void onLinked(File f, String message) {
                        if (f != null) {
                            LinkerGui.showSuccess(f.getAbsolutePath());
                        } else {
                            LinkerGui.showError(message);
                        }
                    }
                });
                linkerGui.gui();
            } else {
                LinkerCli.link(options, inputs);
            }

        } catch (LinkerError le) {
            System.err.println(le.getMessage());
        }
    }

}
