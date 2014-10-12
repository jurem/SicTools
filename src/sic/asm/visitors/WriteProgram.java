package sic.asm.visitors;

import sic.asm.ErrorCatcher;
import sic.ast.Command;
import sic.ast.Comment;
import sic.ast.Program;
import sic.common.Conversion;

import java.io.Writer;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class WriteProgram extends WriteVisitor {

    public WriteProgram(Program program, ErrorCatcher errorCatcher, Writer writer) {
        super(program, errorCatcher, writer);
    }

    public void visit(Command command) {
        int labelLength = program.maxLabelLength();
        int nameLength = 6;
        String n = command.nameToString();
        if (n.startsWith("=") || n.startsWith("+")) { labelLength--; nameLength++; }
        // location address
        w(Conversion.addrToHex(program.locctr()) + "  ");
        // raw code
        w(Conversion.bytesToHexNice(command.emitRawCode(), 4));
        // label
        w(String.format("  %-" + labelLength + "s  ", command.label()));
        // instruction
        w(String.format("%-" + nameLength + "s  ", n));
        // operand
        w(command.operandToString());
        // comment
        w("    " + command.comment());
        w("\n");
    }

    public void visit(Comment comment) {
        w("                  " + comment.comment() + "\n");
    }

}
