package sic.asm.parsing;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.asm.Options;
import sic.ast.Command;
import sic.ast.data.*;
import sic.ast.directives.*;
import sic.ast.expression.Expr;
import sic.ast.instructions.*;
import sic.ast.storage.StorageData;
import sic.ast.storage.StorageRes;
import sic.common.Flags;
import sic.common.Mnemonic;
import sic.common.Opcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Support for parsing of instruction operands.
 *
 * @author jure
 */
public class OperandParser {

    private final Parser parser;

    public OperandParser(Parser parser) {
        this.parser = parser;
    }

    // helpers

    private void checkWhitespace(String name) throws AsmError {
        parser.checkWhitespace("Missing whitespace after mnemonic '%s'", name);
    }

    private List<String> parseSymbols(int maxLength) throws AsmError {
        List<String> syms = new ArrayList<String>();
        do {
            String sym = parser.readSymbol();
            if (sym.length() > maxLength)
                throw new AsmError(parser.loc(), "Symbol name '%s' too long", sym);
            syms.add(sym);
        } while (parser.skipIfComma());
        return syms;
    }

    private Expr parseExpression(boolean throwIfNull) throws AsmError {
        Expr expr = parser.expressionParser.parseExpression();
        if (throwIfNull && expr == null)
            throw new AsmError(parser.loc(), "Expression expected '%c'", parser.peek());
        return expr;
    }

    public Data parseData(int opcode, boolean allowList) throws AsmError {
        Data data;
        switch (parser.peek()) {
            case 'C': data = new DataChr(opcode); break;
            case 'X': data = new DataHex(opcode); break;
            case 'F': data = new DataFloat(opcode); break;
            default: data = new DataNum(opcode); break;
        }
        data.parse(parser, allowList);
        return data;
    }

    private Mnemonic parseLiteralSpec() {
        Mnemonic mnm;
        // WORD, FLOaT, or BYTE (default) literal
        if (parser.advanceIf("BYTE") || parser.advanceIf('B'))
            mnm = parser.mnemonics.get("BYTE");
        else if (parser.advanceIf("FLOT") || parser.peek() == 'F')
            mnm = parser.mnemonics.get("FLOT");
        else {
            mnm = parser.mnemonics.get("WORD");
            parser.advanceIf("WORD"); // WORD is default
            parser.advanceIf('W');
        }
        return mnm;
    }

    private StorageData parseLiteralData() throws AsmError {
        Location loc = parser.loc();
        Mnemonic mnm = parseLiteralSpec();
        parser.skipWhitespace();
        Data data =  parseData(mnm.opcode, false);
        String lbl = parser.program.section().literals.makeUniqLabel();
        return new StorageData(loc, lbl, mnm, data);
    }

    // operand parsing

    private Command parseF1(Location loc, String label, Mnemonic mnemonic) {
        return new InstructionF1(loc, label, mnemonic);
    }

    private Command parseF2n(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        int n = parser.readInt(0, 15);
        return new InstructionF2n(loc, label, mnemonic, n);
    }

    private Command parseF2r(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        int r = parser.readRegister();
        return new InstructionF2r(loc, label, mnemonic, r);
    }

    private Command parseF2rn(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        int r = parser.readRegister();
        parser.skipComma();
        int n = parser.readInt(1, 16);
        return new InstructionF2rn(loc, label, mnemonic, r, n);
    }

    private Command parseF2rr(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        int r1 = parser.readRegister();
        parser.skipComma();
        int r2 = parser.readRegister();
        return new InstructionF2rr(loc, label, mnemonic, r1, r2);
    }

    private Command parseF3(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        return new InstructionF3(loc, label, mnemonic);
    }

    private Command parseF3m(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        checkWhitespace(mnemonic.name);
        Flags flags = new Flags();
        int operand;
        String symbol;
        // check if literal
        if (parser.advanceIf('=')) {
            InstructionF34Base cmd = new InstructionF3m(loc, label, mnemonic, new Flags(Flags.SIMPLE, Flags.NONE), 0, null);
            StorageData lit = parseLiteralData();
            return new InstructionLiteral(cmd, lit);
        }
        // otherwise no literal: detect TA use
        if (parser.advanceIf('#')) flags.set_ni(Flags.IMMEDIATE);
        else if (parser.advanceIf('@')) flags.set_ni(Flags.INDIRECT);
        else flags.set_ni(Flags.SIMPLE);
        // read operand: number, symbol, '*'
        if (Character.isDigit(parser.peek()) || parser.peek() == '-') {
            operand = parser.readInt(flags.minOperand(), flags.maxOperand());
            symbol = null;
        } else if (Character.isLetter(parser.peek()) || parser.peek() == '_') {
            operand = 0;
            symbol = parser.readSymbol();
        } else if (parser.peek() == '*') {
            operand = 0;
            symbol = "*";
        } else
            throw new AsmError(parser.loc(), "Invalid character '%c", parser.peek());
        // check for indexed addressing (only if simple)
        if (parser.skipIfIndexed()) {
            if (flags.isSimple() || flags.isIndirect() && Options.indirectX)
                flags.setIndexed();
            else
                throw new AsmError(parser.loc(), "Indexed addressing not supported here");
        }
        return new InstructionF3m(loc, label, mnemonic, flags, operand, symbol);
    }

    private Command parseF4m(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        checkWhitespace(mnemonic.name);
        Flags flags = new Flags();
        flags.setExtended();
        int operand;
        String symbol;
        // check if literal
        if (parser.advanceIf('=')) {
            InstructionF34Base cmd = new InstructionF4m(loc, label, mnemonic, new Flags(Flags.SIMPLE, Flags.NONE), 0, null);
            StorageData lit = parseLiteralData();
            return new InstructionLiteral(cmd, lit);
        }
        // otherwise no literal: detect TA use
        if (parser.advanceIf('#')) flags.set_ni(Flags.IMMEDIATE);
        else if (parser.advanceIf('@')) flags.set_ni(Flags.INDIRECT);
        else flags.set_ni(Flags.SIMPLE);
        // read operand: number, symbol, '*'
        if (Character.isDigit(parser.peek()) || parser.peek() == '-') {
            operand = parser.readInt(flags.minOperand(), flags.maxOperand());
            symbol = null;
        } else if (Character.isLetter(parser.peek()) || parser.peek() == '_') {
            operand = 0;
            symbol = parser.readSymbol();
        } else if (parser.peek() == '*') {
            operand = 0;
            symbol = "*";
        } else
            throw new AsmError(parser.loc(), "Invalid character '%c", parser.peek());
        // check for indexed addressing (only if simple)
        if (parser.skipIfIndexed()) {
            if (flags.isSimple() || flags.isIndirect() && Options.indirectX)
                flags.setIndexed();
            else
                throw new AsmError(parser.loc(), "Indexed addressing not supported here");
        }
        return new InstructionF4m(loc, label, mnemonic, flags, operand, symbol);    }

    private Command parseD(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        switch (mnemonic.opcode) {
            case Opcode.CSECT:  return new DirectiveCSECT(loc, label, mnemonic);
            case Opcode.LTORG:  return new DirectiveLTORG(loc, label, mnemonic);
            case Opcode.NOBASE: return new DirectiveNOBASE(loc, label, mnemonic);
        }
        return null;
    }

    private Command parseDe(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        Expr expr = parseExpression(true);
        switch (mnemonic.opcode) {
            case Opcode.BASE:  return new DirectiveBASE(loc, label, mnemonic, expr);
            case Opcode.START: return new DirectiveSTART(loc, label, mnemonic, expr);
            case Opcode.END:   return new DirectiveEND(loc, label, mnemonic, expr);
            case Opcode.EQU:   return new DirectiveEQU(loc, label, mnemonic, expr);
        }
        return null;
    }

    private Command parseDe0(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        Expr expr = parseExpression(false);  // expr may be null
        return new DirectiveORG(loc, label, mnemonic, expr);
    }

    private Command parseDs0(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        String blockName = parser.readIfSymbol();
        return new DirectiveUSE(loc, label, mnemonic, blockName);
    }

    private Command parseDs_(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        List<String> names = parseSymbols(6);
        switch (mnemonic.opcode) {
            case Opcode.EXTDEF: return new DirectiveEXTDEF(loc, label, mnemonic, names);
            case Opcode.EXTREF: return new DirectiveEXTREF(loc, label, mnemonic, names);
        }
        return null;
    }

    private Command parseSe(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        Expr expr = parseExpression(true);
        return new StorageRes(loc, label, mnemonic, expr);
    }

    private Command parseSd(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        Data data = parseData(mnemonic.opcode, false);
        return new StorageData(loc, label, mnemonic, data, false);
    }

    private Command parseSd_(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        Data data = parseData(mnemonic.opcode, true);
        return new StorageData(loc, label, mnemonic, data, false);
    }

    // cover all formats

    public Command parse(Location loc, String label, Mnemonic mnemonic) throws AsmError {
        switch (mnemonic.format) {
            case F1:    return parseF1(loc, label, mnemonic);
            case F2n:   return parseF2n(loc, label, mnemonic);
            case F2r:   return parseF2r(loc, label, mnemonic);
            case F2rn:  return parseF2rn(loc, label, mnemonic);
            case F2rr:  return parseF2rr(loc, label, mnemonic);
            case F3:    return parseF3(loc, label, mnemonic);
            case F3m:   return parseF3m(loc, label, mnemonic);
            case F4m:   return parseF4m(loc, label, mnemonic);
            case D:     return parseD(loc, label, mnemonic);
            case De:    return parseDe(loc, label, mnemonic);
            case De0:   return parseDe0(loc, label, mnemonic);
            case Ds0:    return parseDs0(loc, label, mnemonic);
            case Ds_:   return parseDs_(loc, label, mnemonic);
            case Se:    return parseSe(loc, label, mnemonic);
            case Sd:    return parseSd(loc, label, mnemonic);
            case Sd_:   return parseSd_(loc, label, mnemonic);
        }
        return null;
    }

}
