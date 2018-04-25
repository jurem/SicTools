package sic.asm.visitors;

import sic.asm.ErrorCatcher;
import sic.ast.*;
import sic.ast.storage.StorageData;

import java.io.Writer;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class WriteSections extends WriteVisitor {

    private final int symLen;

    public WriteSections(Program program, ErrorCatcher errorCatcher, Writer writer) {
        super(program, errorCatcher, writer);
        this.symLen = program.maxSymbolLength();
    }

    public void visit(Program program) {
        visitSections(program.sections);
    }

    public void visit(Symbol sym) {
        w(String.format("    %-" + symLen + "s  %06X  %8d  %-8s  %-8s  %-8s  %s\n", sym.name, sym.value(), sym.value(), sym.scopeToString(), sym.kindToString(), sym.labelTypeToString(), sym.exprToString()));
    }

    public void visit(Section section) {
        w("***** Section " + section.name() + " *****\n");
        w(String.format("Stats: size=%d  blocks=%d  symbols=%d  literals=%d  relocations=%d\n",
                section.size(), section.block().size(), section.symbols.asSortedList().size(),
                section.literals.size(), section.relocations.size()));

        // blocks
        w("Blocks\n");
        w("    name        start   size  #ins #dir #sto\n");
        visitBlocks(section.blocks);
        // symbols
        w("Symbols\n");
        w(String.format("    %-" + symLen + "s     hex       dec  scope     kind      type      description\n", "name"));
        visitSymbols(section.symbols.asSortedList());
        // literals
        w("Literals\n");
        int l = section.literals.maxLabelLength();
        w("    %-" + l + "s  definition\n", "label");
        for (StorageData lit : section.literals.literals)
            w("    %-"+l+"s  %s\n", lit.label(), lit.toString());
        // relocations
        w("Relocations\n");
        w("    address length flag symbol\n");
        for (Relocation r : section.relocations)
            if (r.hasSymbol())
                w("    %05X   %6d  %c    %s\n", r.address, r.length, r.flag, r.symbol);
            else
                w("    %05X   %6d\n", r.address, r.length);
    }

    public void visit(Block block) {
        w(String.format("    %-10s  %05X  %05X  %4d %4d %4d\n",
                block.name(), block.start(), block.size(), block.countInstructions(), block.countDirectives(), block.countStorages()));
    }

}
