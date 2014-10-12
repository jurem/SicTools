package sic.asm;

import sic.asm.parsing.Parser;
import sic.asm.visitors.*;
import sic.ast.Program;
import sic.common.Mnemonics;

import java.io.Writer;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Assembler {

    public final ErrorCatcher errorCatcher;
    public final Mnemonics mnemonics;
    public final Parser parser;

    public Assembler() {
        this.errorCatcher = new ErrorCatcher();
        this.mnemonics = new Mnemonics();
        this.parser = new Parser(mnemonics, errorCatcher);
    }

    public Program assemble(String input) {
        errorCatcher.clear();

        // phase zero: parse the source code
        parser.begin(input);
        Program program = parser.parseProgram();

        // phase one: absolute expressions
        new DefineEQUs(program, errorCatcher).visitCommands();             // define EQUs
        new EvalEQUs(program, errorCatcher, false).visitCommands();        // EQUs: resolve absolute expressions
        new ResolveAbsolute(program, errorCatcher).visitCommands();        // resolve START and RESx

        // phase two: relative expressions
        new ResolveBlocks(program, errorCatcher).visitByStructure();       // define labels, resolve block sizes, resolve ORGs
        new EvalEQUs(program, errorCatcher, true).visitCommands();         // resolve also relative EQUs
        new ResolveRelative(program, errorCatcher).visitCommands();        // resolve BASE and import/export
        new ResolveSymbols(program, errorCatcher).visitCommands();         // resolve instructions
        // TODO: check for undefined symbols, unevaluated expressions

        return program;
    }

    public void generateListing(Program program, Writer writer) {
        new WriteProgram(program, errorCatcher, writer).visitCommands();
    }

    public void generateLog(Program program, Writer writer) {
        new WriteSections(program, errorCatcher, writer).visitByStructure();
    }

    public void generateObj(Program program, Writer writer, boolean addSpaceInObj) {
        new WriteText(program, errorCatcher, writer, addSpaceInObj).visitByStructure();
    }

}
