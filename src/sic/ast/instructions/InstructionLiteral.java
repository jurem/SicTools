package sic.ast.instructions;

import sic.asm.AsmError;
import sic.ast.Program;
import sic.ast.storage.StorageData;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class InstructionLiteral extends Instruction {

    public final InstructionF34Base command;
    protected StorageData literal;

    public InstructionLiteral(InstructionF34Base command, StorageData literal) {
        super(command.loc, command.label, command.mnemonic);
        this.command = command;
        this.literal = literal;
    }

    @Override
    public String toString() {
        return command.toString() + " " + operandToString();
    }

    public void append(Program program) throws AsmError {
        enter(program);
        StorageData lit = program.section().literals.find(literal);
        if (lit != null) literal = lit;
        else program.section().literals.append(literal);
        command.setSymbol(literal.label);
        program.append(this);       // add command to the current block
        leave(program);
    }

    @Override
    public int size() {
        if (command == null) return 3;//TODO
        return command.size();
    }

    @Override
    public String operandToString() {
        return command.symbol +  " (" + literal.toString() + ")";
    }

    @Override
    public void emitRawCode(byte[] data, int loc) {
        command.emitRawCode(data, loc);
    }

}
