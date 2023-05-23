package sic.sim;

import java.util.Vector;

import sic.common.Utils;

/**
 * Created by jure on 9. 02. 16.
 */

//TODO
class AbstractCmdLineArgs {

    String[] args;
    int pos;

    public void AbstractCmdLineArgs(String[] args) {
        this.args = args;
        pos = 0;
    }

    public boolean hasNext() {
        return pos < args.length;
    }

    public String next() {
        return args[pos];
    }

    public int nextInt() {
        return 0;
    }
}


public class Args extends  AbstractCmdLineArgs {

    private boolean help;

    private String filename;
    private String filebase;
    private String fileext;

    private int freq;
    private int debug;
    private boolean start;
    private boolean stats;

    private boolean textScr;
    private String textScrPars;

    private boolean graphScr;
    private int graphScrCols;
    private int graphScrRows;
    private int graphScrFreq;

    private boolean keyb;
    private String keybPars;

    private Vector<AddonArgs> addons = new Vector<AddonArgs>();

    public boolean isHelp() {
        return help;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilebase() {
        return filebase;
    }

    public String getFileext() {
        return fileext;
    }

    public boolean hasFilename() {
        return filename != null;
    }

    public int getFreq() {
        return freq;
    }

    public int getDebug() {
        return debug;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isStats() {
        return stats;
    }

    public boolean isTextScr() {
        return textScr;
    }

    public String getTextScrPars() {
        return textScrPars;
    }

    public boolean isGraphScr() {
        return graphScr;
    }

    public int getGraphScrCols() {
        return graphScrCols;
    }

    public int getGraphScrRows() {
        return graphScrRows;
    }

    public boolean isKeyb(){
        return keyb;
    }

    public String getKeybPars(){
        return keybPars;
    }

    public int getGraphScrFreq() {
        return graphScrFreq;
    }

    public Vector<AddonArgs> getAddons() {
        return addons;
    }

    public static void printArgs() {
        System.out.print(
            "    -help|-h              Print help.\n" +
            "    -freq hz              Set the machine frequency.\n" +
          //"    -debug level      Set the debug level.\n" + // Don't display while not implemented (TODO)
            "    -start                Start on load.\n" +
            "    -stats                Print instruction statistics.\n" +
            "    -text colsxrows       Show and resize textual screen.\n" +
            "    -graph colsxrows[@hz] Show and resize graphical screen.\n" +
            "    -keyb address         Show and set keyboard address.\n" +
            "    -a path[@params]      Load addon.\n");
    }

    int parseFreq(String s) {
        return Integer.parseInt(s);
    }

    void parseGraphScreen(String s) {
        int x = s.indexOf('x');
        int at = s.indexOf('@');

        String cols = s.substring(0, x);
        String rows;
        String hz;
        if (at != -1) {
            rows = s.substring(x + 1, at);
            hz = s.substring(at + 1);
        } else {
            rows = s.substring(x + 1);
            hz = "120";
        }

        graphScrCols = Integer.parseInt(cols);
        graphScrRows = Integer.parseInt(rows);
        graphScrFreq = Integer.parseInt(hz);
    }

    void parseAddon(String s) {
        int i = s.indexOf('@');
        String path = s;
        String params = null;
        if (i != -1) {
            path = s.substring(0, i);
            params = s.substring(i + 1);
        }
        addons.add(new AddonArgs(path, params));
    }

    void processArgs(String[] args) {
        // options
        int last = 0;
        while (last < args.length) {
            String arg = args[last];
            switch (arg) {
                case "-help":
                case "-h":
                    help = true;
                    break;
                case "-stats":
                    stats = true;
                    break;
                case "-start":
                    start = true;
                    break;
                case "-freq":
                    freq = parseFreq(args[++last]);
                    break;
                case "-debug":
                    debug = Integer.parseInt(args[++last]);
                    break;
                case "-text":
                    textScr = true;
                    textScrPars = args[++last];
                    break;
                case "-graph":
                    graphScr = true;
                    parseGraphScreen(args[++last]);
                    break;
                case "-keyb":
                    keyb = true;
                    keybPars = args[++last];
                    break;
                case "-a":
                    parseAddon(args[++last]);
                    break;
            }
            if (!arg.startsWith("-") || arg.equals("--")) break;
            last++;
        }
        // parameters
        if (last < args.length) {
            filename = args[last++];
            filebase = Utils.getFileBasename(filename);
            fileext = Utils.getFileExtension(filename);
        }
    }

    public Args(String[] args) {
        processArgs(args);
    }

    public class AddonArgs {
        public String path;
        public String pars;

        public AddonArgs(String path, String pars) {
            this.path = path;
            this.pars = pars;
        }
    }
}
