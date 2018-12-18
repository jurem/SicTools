package sic.asm.parsing;

import sic.asm.AsmError;
import sic.asm.ErrorCatcher;
import sic.asm.Location;
import sic.asm.Options;
import sic.ast.Command;
import sic.ast.Comment;
import sic.ast.Program;
import sic.common.Mnemonic;
import sic.common.Mnemonics;


/**
 * Parser
 *
 * @author jure
 */
public class Parser extends Lexer {

    public final Mnemonics mnemonics;
    public final OperandParser operandParser;
    public final ExpressionParser expressionParser;
    public final ErrorCatcher errorCatcher;

    // dynamic
    protected Program program;

    public Parser(Mnemonics mnemonics, ErrorCatcher errorCatcher) {
        this.mnemonics = mnemonics;
        this.operandParser = new OperandParser(this);
        this.expressionParser = new ExpressionParser(this, true);
        this.errorCatcher = errorCatcher;
    }

    public void checkWhitespace(String fmt, Object... objs) throws AsmError {
        if (Options.requireWhitespace) {
            if (available() <= 0) throw new AsmError(loc(), true,"Empty label %s at the end", objs);
            if (!Character.isWhitespace(prev)) throw new AsmError(loc(), fmt, objs);
        }
    }

    public Command parseIfCommand() throws AsmError {
        Location loc = loc();
        String label = readIfLabel();
        skipWhitespace();
        if (label != null) {
            skipLinesAndComments();
            checkWhitespace("Missing whitespace after label '%s'", label);
        }
        else loc = loc();
        String name = readIfMnemonic();
        if (name == null) {
            if (label == null) return null; // no instruction present
            throw new AsmError(loc(), "Missing mnemonic");
        }
        // name != null
        Mnemonic mnemonic = mnemonics.get(name);
        if (mnemonic == null)
            throw new AsmError(loc(), "Invalid mnemonic '%s'", name);
        skipWhitespace();
        return operandParser.parse(loc, label, mnemonic);
    }

    public Program parseProgram() {
        program = new Program();
        // advance to the beginning of command
        while (ready() && col > 1)
            advanceUntil('\n');
        // do the lines
        while (ready()) {
            assert col() == 1;
            Location lineLoc = loc();
            Command command;
            String comment;
            try {
                command = parseIfCommand();
                skipWhitespace();
                comment = readIfComment(Options.requireCommentDot, Options.skipEmptyLines);
                if (command == null && comment == null) advance('\n'); // advance over the empty line
            } catch (AsmError e) {
                errorCatcher.add(e);
                advanceUntil('\n');
                continue;
            }
            // check what we got
            try {
                if (command != null) {
                    // command with possible comment
                    command.setComment(comment);
                    command.append(program);
                } else if (comment != null) {
                    // only comment
                    command = new Comment(lineLoc, comment);
                    command.append(program);
                } else {
                    if (!Options.skipEmptyLines) {
                        command = new Comment(lineLoc, "");
                        command.append(program);
                    }
                }
            } catch (AsmError e) {
                errorCatcher.add(e);
            }
        }
        try {
            program.flushAllLiterals();
        } catch (AsmError e) {
            errorCatcher.add(e);
        }
        return program;
    }

}
