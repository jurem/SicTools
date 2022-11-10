package sic;

import sic.asm.Assembler;
import sic.asm.ErrorCatcher;
import sic.asm.Options;
import sic.ast.Program;
import sic.common.Mnemonics;
import sic.common.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Sic/XE assembler.
 *
 * @author jure
 */
public class Asm {

    public static final int Version_Major = 2;
    public static final int Version_Minor = 0;
    public static final int Version_Patch = 1;

    //
    private boolean stdin;
    private String input;
    private Writer lstwriter;
    private Writer logwriter;
    private Writer objwriter;

    static void printHelp() {
        System.out.print(
        "Sic/XE Assembler " + Version_Major + "." + Version_Minor + "." + Version_Patch + "\n" +
        "Usage: java sic.Asm options parameters\n" +
        "Options:\n" +
        "    -help|-h       Print help.\n" +
        "    -refshort      Print short assembly reference.\n" +
        "    -reflong|-ref  Print long assembly reference.\n" +
        "\n" +
        "    -obj-dense         Dense object files (without space).\n" +
        "    -obj-slack         Slack object files (with space).\n" +
        "    -space-require     Require whitespace after labels and mnemonics.\n" +
        "    -space-forgo\n" +
        "    -comment-dot-require  Require dots in comments.\n" +
        "    -comment-dot-forgo.\n" +
        "    -indirect-x        Allow indirect indexed addressing.\n"
        );
    }

    void processArgs(String[] args) {
        if (args.length > 0) {
            if ("-help".equals(args[0]) || "-h".equals(args[0])) {
                printHelp();
                System.exit(0);
            }
            if ("-refshort".equals(args[0])) {
                new Mnemonics().printReferenceShort();
                System.exit(0);
            }
            if ("-reflong".equals(args[0]) || "-ref".equals(args[0])) {
                new Mnemonics().printReferenceLong();
                System.exit(0);
            }
        }
        // assembler flags
        int last = 0;
        while (last < args.length) {
            String arg = args[last];
            if ("-obj-dense".equals(arg)) Options.addSpaceInObj = false;
            if ("-obj-slack".equals(arg)) Options.addSpaceInObj = true;
            if ("-space-require".equals(arg)) Options.requireWhitespace = true;
            if ("-space-forgo".equals(arg)) Options.requireWhitespace = false;
            if ("-comment-dot-require".equals(arg)) Options.requireCommentDot = true;
            if ("-comment-dot-forgo".equals(arg)) Options.requireCommentDot = false;
            if ("-indirect-x".equals(arg)) Options.indirectX = true;
            if (!arg.startsWith("-")) break;
            last++;
        }
        // use standard input?
        if (last >= args.length) {
            stdin = true;
            input = Utils.readStdin();
            lstwriter = new BufferedWriter(new OutputStreamWriter(System.out));
            logwriter = lstwriter;
            objwriter = lstwriter;
            return;
        }
        // prepare files
        String filename = args[last];
        input = Utils.readFile(filename);
        String basename = Utils.getFileBasename(filename);
        lstwriter = Utils.createFileWriter(basename + ".lst");
        logwriter = Utils.createFileWriter(basename + ".log");
        objwriter = Utils.createFileWriter(basename + ".obj");
    }

    void processSource() {
        Assembler assembler = new Assembler();
        ErrorCatcher errorCatcher = assembler.errorCatcher;
        Program program = assembler.assemble(input);
        if (errorCatcher.count() > 0) {
            //new WriteErrors(program, errorCatcher).visitCommands();
            errorCatcher.print();
            return;
        }
        //
        try {
            if (stdin) lstwriter.write("******************** Program *******************\n");
            assembler.generateListing(program, lstwriter);
            lstwriter.flush();
            if (stdin) logwriter.write("******************** Structure *****************\n");
            assembler.generateLog(program, logwriter);
            logwriter.flush();
            if (stdin) objwriter.write("********************** Text ********************\n");
            assembler.generateObj(program, objwriter, Options.addSpaceInObj);
            objwriter.flush();
            if (!stdin) {
                lstwriter.close();
                logwriter.close();
                objwriter.close();
            }
        } catch (IOException e) {
            System.err.println("Error while generating files.");
        }
        errorCatcher.print();
    }

    public static void main(String[] args) {
        Asm asm = new Asm();
        asm.processArgs(args);
        asm.processSource();
    }

}

/* TODO: assembler todo list
assembler arguments
-sic ... use SIC
-sicxe ... use SIC/XE
-nosic ... do not use old SIC format
*/