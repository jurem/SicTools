package sic.ast;

import sic.asm.AsmError;
import sic.common.SICXE;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Section extends Node {

    public final String name;                   // name of the section
    public final List<Block> blocks;            // list of blocks
    public final Symbols symbols;               // symbol table
    public final Literals literals;             // literals
    public final List<Relocation> relocations;  // relocations
    private int size;

    private Block block;                        // current active block
    private int baseRegister;		            // base register (undefined if -1)

    public Section(String name) {
        super();
        this.name = name;
        blocks = new ArrayList<Block>();
        symbols = new Symbols();
        literals = new Literals();
        relocations = new ArrayList<Relocation>();
        reset();
    }

    @Override
    public String toString() {
        return name();
    }

    public boolean isDefault() {
        return "".equals(name);
    }

    public String name() {
        return isDefault() ? "<default>" : name;
    }

    @Override
    public void enter(Program program) throws AsmError {
        program.switchSection(name);
        program.section().switchBlock("");
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }

    public void reset() {
        switchBlock("");
        disableBaseAddressing();
    }

    // blocks

    public Block block() {
        return block;
    }

    public Block findBlock(String name) {
        for (Block block : blocks)
            if (block.name.equals(name)) return block;
        return null;
    }

    public void switchBlock(String name) {
        block = findBlock(name);
        if (block == null) {
            block = new Block(name);
            blocks.add(block);
        }
    }

    // pc-relative addressing

    public int PCDisplacement(int addr) {
        return addr - block.nextLocctr();
    }

    public boolean isPCRelativeAddressing(int addr) {
        return SICXE.isSdisp(PCDisplacement(addr));
    }

    // base-relative addressing

    public void enableBaseAddressing(int valueB) {
        assert valueB >= 0;
        baseRegister = valueB;
    }

    public void disableBaseAddressing() {
        baseRegister = -1;
    }

    public int baseDisplacement(int addr) {
        return addr - baseRegister;
    }

    public boolean isBaseAddressing(int addr) {
        return baseRegister >= 0 && SICXE.isDisp(baseDisplacement(addr));
    }

    // relocations

    private void addRelocation(Relocation relocation) {
        relocations.add(relocation);
    }

    public void addRelocation(int address, int length) {
        addRelocation(new Relocation(address, length));
    }

    public void addRelocation(int address, int length, char flag, String symbol) {
        addRelocation(new Relocation(address, length, flag, symbol));
    }

}
