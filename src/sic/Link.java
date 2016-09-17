package sic;

import sic.link.*;
import sic.link.ui.LinkListener;
import sic.link.ui.LinkerCli;
import sic.link.ui.LinkerGui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/*
 * SIC/XE Linker
 *
 * start linker by calling this class
 * run "java sic.Link -help" for all available options or
 *     "java sic.Link -gui" for graphical interface
 *
 */
public class Link {

    /*
     * processes args into Options class and list of input files
     * calls LinkerGui or LinkerCli based on entered options
     */
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
