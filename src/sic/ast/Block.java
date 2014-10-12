package sic.ast;

import sic.asm.AsmError;
import sic.ast.directives.Directive;
import sic.ast.instructions.Instruction;
import sic.ast.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * A block of assembler commands (comments, directives, or instructions).
 *
 * @author jure
 */
public class Block extends Node {

    /** Name of the block. */
    public final String name;
    /** List of commands in the block. */
    public final List<Command> commands;
    private int countInstruction;
    private int countDirective;
    private int countStorage;

    private int start;                  // start address of the block
    private int locctr;			        // address of the instruction currently being assembled
    private int nextLocctr;		        // as program counter register
    private int lastLocctr;             // last locctr before ORG
    private int size;                   // size of code

    /**Constructs new block with a given name.
     * @param name name of a new block */
    public Block(String name) {
        this.name = name;
        commands = new ArrayList<Command>();
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

    /**Appends a command to the list of commands.
     * @param command command to be appended
     */
    public void append(Command command) {
        commands.add(command);
        if (command instanceof Instruction) countInstruction++;
        if (command instanceof Directive) countDirective++;
        if (command instanceof Storage) countStorage++;
    }

    public int countInstructions() {
        return countInstruction;
    }

    public int countDirectives() {
        return countDirective;
    }

    public int countStorages() {
        return countStorage;
    }

    public void reset() {
        setLocctr(start);
        lastLocctr = -1;
    }

    public int start() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
        setLocctr(start);
    }

    public int locctr() {
        return locctr;
    }

    public void setLocctr(int addr) {
        locctr = nextLocctr = addr;
    }

    public int nextLocctr() {
        return nextLocctr;
    }

    public void step(int size) {
        locctr = nextLocctr;
        nextLocctr += size;
    }

    public void setOrigin(int addr) {
        if (lastLocctr < 0) {
            lastLocctr = nextLocctr;
        }
        setLocctr(addr);
    }

    public void restoreLocctr() {
        if (lastLocctr >= 0) {
            setLocctr(lastLocctr);
            lastLocctr = -1;
        }
    }

    public int size() {
        return size;
    }

    @Override
    public void enter(Program program) throws AsmError {
        program.section().switchBlock(name);
    }

    @Override
    public void leave(Program program) throws AsmError {
        size = nextLocctr - start;  // start = 0 on this pass (VisitByStructure)
    }

}
