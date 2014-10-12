package sic.common;

/**
 * Instruction operand codes
 * @author jure
 */
public class Opcode {

    // ************ Instructions ****************

    // Not-used opcodes
    // 0x8C, 0xBC, 0xCC, 0xE4, 0xFC

    // ***** SIC format, SIC/XE format 3, and SIC/XE format 4 *****

    // load and store
    public static final int LDA = 0x00;
    public static final int LDX = 0x04;
    public static final int LDL = 0x08;
    public static final int STA = 0x0C;
    public static final int STX = 0x10;
    public static final int STL = 0x14;
    // fixed point arithmetic
    public static final int ADD = 0x18;
    public static final int SUB = 0x1C;
    public static final int MUL = 0x20;
    public static final int DIV = 0x24;
    public static final int COMP = 0x28;
    public static final int TIX = 0x2C;
    // jumps
    public static final int JEQ = 0x30;
    public static final int JGT = 0x34;
    public static final int JLT = 0x38;
    public static final int J = 0x3C;
    // bit manipulation
    public static final int AND = 0x40;
    public static final int OR = 0x44;
    // jump to subroutine
    public static final int JSUB = 0x48;
    public static final int RSUB = 0x4C;
    // load and store int
    public static final int LDCH = 0x50;
    public static final int STCH = 0x54;

    // ***** SICXE Format 3 and Format 4

    // floating point arithmetic
    public static final int ADDF = 0x58;
    public static final int SUBF = 0x5C;
    public static final int MULF = 0x60;
    public static final int DIVF = 0x64;
    public static final int COMPF = 0x88;
    // load and store
    public static final int LDB = 0x68;
    public static final int LDS = 0x6C;
    public static final int LDF = 0x70;
    public static final int LDT = 0x74;
    public static final int STB = 0x78;
    public static final int STS = 0x7C;
    public static final int STF = 0x80;
    public static final int STT = 0x84;
    // special load and store
    public static final int LPS = 0xD0;
    public static final int STI = 0xD4;
    public static final int STSW = 0xE8;
    // devices
    public static final int RD = 0xD8;
    public static final int WD = 0xDC;
    public static final int TD = 0xE0;
    // system
    public static final int SSK = 0xEC;

    // ***** SIC/XE Format 1 *****

    public static final int FLOAT = 0xC0;
    public static final int FIX = 0xC4;
    public static final int NORM = 0xC8;
    public static final int SIO = 0xF0;
    public static final int HIO = 0xF4;
    public static final int TIO = 0xF8;

    // ***** SIC/XE Format 2 *****

    public static final int ADDR = 0x90;
    public static final int SUBR = 0x94;
    public static final int MULR = 0x98;
    public static final int DIVR = 0x9C;
    public static final int COMPR = 0xA0;
    public static final int SHIFTL = 0xA4;
    public static final int SHIFTR = 0xA8;
    public static final int RMO = 0xAC;
    public static final int SVC = 0xB0;
    public static final int CLEAR = 0xB4;
    public static final int TIXR = 0xB8;

    private static final String[] opcodeToNames = {
        "LDA", "LDX", "LDL", "STA", "STX", "STL", "ADD", "SUB",
        "MUL", "DIV", "COMP", "TIX", "JEQ", "JGT", "JLT", "J",
        "AND", "OR", "JSUB", "RSUB", "LDCH", "STCH", "ADDF", "SUBF",
        "MULF", "DIVF", "LDB", "LDS", "LDF", "LDT", "STB", "STS",
        "STF", "STT", "COMPF", null, "ADDR", "SUBR", "MULR", "DIVR",
        "COMPR", "SHIFTL", "SHIFTR", "RMO", "SVC", "CLEAR", "TIXR", null,
        "FLOAT", "FIX", "NORM", null, "LPS", "STI", "RD", "WD",
        "TD", null, "STSW", "SSK", "SIO", "HIO", "TIO", null
    };

    public static String getName(int opcode) {
        // 0 <= opcode <= 256
        return opcodeToNames[opcode >> 2];
    }

    public static boolean isValid(int opcode) {
        return getName(opcode) != null;
    }

    // ************ Directives ******************

    public static final int START       = 0;
    public static final int END         = 1;

    public static final int CSECT       = 2;
    public static final int USE         = 3;
    public static final int EXTREF      = 4;
    public static final int EXTDEF      = 5;

    public static final int ORG         = 6;
    public static final int LTORG       = 7;

    public static final int BASE        = 8;
    public static final int NOBASE      = 9;

    public static final int EQU         = 10;

    // Storage directives

    public static final int RESB        = 0;
    public static final int RESW        = 1;
    public static final int RESF        = 2;
    public static final int BYTE        = 3;
    public static final int WORD        = 4;
    public static final int FLOT        = 5;

}
