package sic.link;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Options for the linker
 */
public class Options {
    private static final String PHASE = "options";

    private String outputName = null; // specifies the output file name
    private String outputPath = null; // specifies the output path
    private boolean force = false;    // force linking even if not all ext symbols are in the files
    private String main = null;       // first section - otherwise the first in the first input file is used
    private boolean verbose = false;  // displays debugging messages during linking
    private boolean keep = false;     // keep the D records in the file - to allow further linking
    private boolean graphical = false;      // open the gui

    /*
     * processes option flags
     */
    public int processFlags(String[] args) throws LinkerError {
        int processedArgs = 0;

        for(int i=0; i<args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-")) {
                switch (arg) {
                    case "-o":
                        //specifies the output file
                        i++;
                        if (i == args.length)
                            throw new LinkerError(PHASE, "Output file name not specified");
                        String output = args[i];

                        File file = new File(output);

                        if (file.isDirectory() || file.getParentFile() != null && !file.getParentFile().exists())
                            throw new LinkerError(PHASE, output + " is not a valid output file");

                        this.outputName = file.getName();
                        this.outputPath = file.getAbsolutePath();

                        processedArgs += 2;
                        break;

                    case "-f":
                        // forces linking even if not all external symbols are present
                        this.force = true;
                        processedArgs++;
                        break;

                    case "-k":
                        // keeps D records in the output file
                        this.keep = true;
                        processedArgs++;
                        break;

                    case "-m":
                        // specifies the first (main) section
                        i++;
                        if (i == args.length)
                            throw new LinkerError(PHASE, "Main section name not specified");
                        this.main = args[i];
                        processedArgs += 2;
                        break;

                    case "-v":
                        // verbose mode
                        this.verbose = true;
                        processedArgs++;
                        break;

                    case "-g":
                        // graphical mode
                        this.graphical = true;
                        processedArgs++;
                        break;

                    case "-h":
                    case "help":
                    default:
                        // display help end exit
                        System.out.println("SIC/XE Linker");
                        System.out.println();

                        System.out.println("Links given relative object files, resolving the references between them using the R and D records." +
                                " The result is an .obj file, with one relative control section that can be loaded and executed.");

                        System.out.println();
                        System.out.println("Usage:");
                        System.out.println("java sic.Link <options> <input files> ");

                        System.out.println();
                        System.out.println("Linker options:");
                        System.out.println("-o <path to a file> : specifies the output file");
                        System.out.println("-m <section name> : specifies the main/starting section");
                        System.out.println();
                        System.out.println("-f : force linking even if not all references can be resolved");
                        System.out.println("-k : keep D records in the output file");
                        System.out.println("-v : display debugging messages during linking");
                        System.out.println("-g : displays a graphical interface");
                        System.out.println();
                        System.out.println("-h : shows this help");

                        throw new LinkerError(null); // throw error to end execution

                }
            }
            else
                break;
        }
        return processedArgs;
    }

    public String getOutputName() {
        return outputName;
    }


    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public boolean isGraphical() {
        return graphical;
    }

    public void setGraphical(boolean graphical) {
        this.graphical = graphical;
    }

    public String describeOptions() {
        StringBuilder builder = new StringBuilder();
        if (main != null)
            builder.append(" -m " + main);
        if (force)
            builder.append(" -f");
        if (verbose)
            builder.append(" -v");
        if (keep)
            builder.append(" -k");

        return builder.toString();
    }
}
