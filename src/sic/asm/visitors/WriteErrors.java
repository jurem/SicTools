package sic.asm.visitors;

import sic.asm.ErrorCatcher;
import sic.ast.Command;
import sic.ast.Comment;
import sic.ast.Program;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class WriteErrors extends Visitor {

    public WriteErrors(Program program, ErrorCatcher errorCatcher) {
        super(program, errorCatcher);
    }

    private void p(String str) {
        System.err.print(str);
    }

    public void visit(Command command) {
        int labelLength = program.maxLabelLength();
        int nameLength = 6;
        String n = command.nameToString();
        if (n.startsWith("=") || n.startsWith("+")) { labelLength--; nameLength++; }
        // location address
//        w(Conversion.addrToHex(program.locctr()) + "  ");
        // raw code
//        w(Conversion.bytesToHexNice(command.emitRawCode(), 4));
        // label
        p(String.format("  %-" + labelLength + "s  ", command.label()));
        // instruction
        p(String.format("%-" + nameLength + "s  ", n));
        // operand
        p(command.operandToString());
        // comment
        p("    " + command.comment());
        p("\n");
        errorCatcher.printByRow(command.loc.row);
    }

    public void visit(Comment comment) {
        p(comment.comment() + "\n");
    }

}
