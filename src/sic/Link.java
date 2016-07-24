package sic;

import sic.link.Linker;
import sic.link.LinkerError;

import java.util.ArrayList;
import java.util.List;

/**
 * SIC/XE Linker
 *
 * Created by nejc on 02/07/16.
 */
public class Link {

    public static void main(String[] args) {

        // get all options
        List<String> options = processFlags(args);

        // get all the input files
        List<String> inputs = processInputs(args, options.size());

        Linker linker = new Linker(inputs, options);
        try {
            linker.link();
        } catch (LinkerError le) {
            System.err.println(le.getMessage());
        }
    }

    /*
    * processes option flags and returns their count
    * */
    private static List<String> processFlags(String[] args) {
        List<String> options = new ArrayList<>();

        for(int i=0; i<args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-"))
               options.add(arg.substring(1));
            else
                break;
        }

        return options;
    }

    private static List<String> processInputs(String[] args, int start) {
        List<String> inputs = new ArrayList<>();

        for (int i=start; i<args.length; i++)
            inputs.add(args[i]);

        return inputs;
    }

}
