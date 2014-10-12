package sic.asm.visitors;

import sic.asm.AsmError;
import sic.asm.ErrorCatcher;
import sic.ast.*;
import sic.ast.directives.DirectiveORG;

import java.io.Writer;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class WriteText extends WriteVisitor {

    public final boolean addSpace;
    public final String space;

    public WriteText(Program program, ErrorCatcher errorCatcher, Writer writer, boolean addSpace) {
        super(program, errorCatcher, writer);
        this.addSpace = addSpace;
        this.space = addSpace ? " " : "";
    }

    // text-record builder

    private StringBuilder buf = new StringBuilder();
    private int textAddr;

    private void flushBuf() {
        if (buf.length() == 0) return;
        w("T%s%06X%s%02X", space, textAddr, space, buf.length() / 2);
        w(buf.toString());
        w("\n");
        buf = new StringBuilder();
    }

    // visitors

    public void visit(Program program) {
        visitSections(program.sections);
    }

    public void visit(Section section) {
        // header record
        int start = "".equals(section.name) ? program.start() : 0;
        String name = "".equals(section.name) ? program.name() : section.name();
        w("H%s%-6s%s%06X%s%06X\n", space, name, space, start, space, section.size());
        // define records (exported symbols)
        int cnt = 0;
        for (Symbol sym : section.symbols.asSortedList()) if (sym.isExported()) {
            if (sym.name.equals(name)) continue;  // name of the programm is automatically exported
            if (cnt == 0) w("D");
            w("%s%-6s%s%06X ", space, sym.name, space, sym.value() - start);
            if (++cnt >= 6) { cnt = 0; w("\n"); }
        }
        if (cnt > 0) w("\n");
        // refer records (imported symbols)
        cnt = 0;
        for (Symbol sym : section.symbols.asSortedList()) if (sym.isImported()) {
            if (cnt == 0) w("R");
            w("%s%-6s", space, sym.name);
            if (++cnt >= 12) { cnt = 0; w("\n"); }
        }
        if (cnt > 0) w("\n");
        // text records
        textAddr = start;
        visitBlocks(section.blocks);
        flushBuf();
        // modification records
        for (Relocation r : section.relocations)
            w(r.flag == 0 ? String.format("M%s%06X%s%02X\n", space, r.address, space, r.length)
                            :
                            String.format("M%s%06X%s%02X%s%c%s\n", space, r.address, space, r.length, space, r.flag, r.symbol)
        );
        // end record
        int first = "".equals(section.name) ? program.first() : start;
        w("E%s%06X\n", space, first);
    }

    public void visit(Block block) throws AsmError {
        visitCommands(block.commands);
    }

    public void visit(Command c) throws AsmError {
        if (c.size() > 0) buf.append(space);
        boolean flush = c.emitText(buf);
        if (flush || buf.length() > 56) {
            flushBuf();
            textAddr = program.locctr() + c.size();
        }
    }

    public void visit(DirectiveORG d) {
        flushBuf();
    }

}
