package sic.asm.visitors;

import sic.asm.ErrorCatcher;
import sic.ast.Program;

import java.io.IOException;
import java.io.Writer;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class WriteVisitor extends Visitor {

    protected final Writer writer;

    public WriteVisitor(Program program, ErrorCatcher errorCatcher, Writer writer) {
        super(program, errorCatcher);
        this.writer = writer;
    }

    protected void w(String str) {
        try {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void w(String fmt, Object... objs) {
        w(String.format(fmt, objs));
    }

}
