package sic.common;

import java.util.*;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Mnemonics {

    public final Map<String, Mnemonic> map;

    public Mnemonics() {
        this.map = new HashMap<String, Mnemonic>();
        initMnemonics();
    }

    public Mnemonic get(String name) {
        if (map.containsKey(name)) return map.get(name);
        return null;
    }

    public void put(Mnemonic mnemonic) {
        map.put(mnemonic.name, mnemonic);
    }

    public void put(String name, int opcode, Format format, String hint, String desc) {
        put(new Mnemonic(name, opcode, format, hint, desc));
    }

    public void put(String name, Format format, String hint, String desc) {
        put(new Mnemonic(name, (byte)0, format, hint, desc));
    }

    public void put34(String name, int opcode, String hint, String desc) {
        put(new Mnemonic(name, opcode, Format.F3m, hint, desc));
        put(new Mnemonic("+" + name, opcode, Format.F4m, hint, desc));
    }

    public List<Mnemonic> sortByKindName() {
        List<Mnemonic> list = new ArrayList<Mnemonic>(map.values());
        Collections.sort(list, new Comparator<Mnemonic>() {
            public int compare(Mnemonic o1, Mnemonic o2) {
                String n1 = o1.getClass().getName();
                String n2 = o2.getClass().getName();
                int r = n1.compareTo(n2);
                if (r != 0) return r;
                return o1.name.compareTo(o2.name);
            }
        });
        return list;
    }

    public List<Mnemonic> sortByName() {
        List<Mnemonic> list = new ArrayList<Mnemonic>(map.values());
        Collections.sort(list, new Comparator<Mnemonic>() {
            public int compare(Mnemonic o1, Mnemonic o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return list;
    }

    public List<String> getReferenceShort() {
        final String formatRef = "%-6s  %-10s";
        List<String> list = new ArrayList<String>();
        for (Mnemonic m : sortByKindName()) {
            if (m.isExtended()) continue;
            list.add(String.format(formatRef, m.name, m.format.hint()));
        }
        return list;
    }

    public void printReferenceShort() {
        List<String> list = getReferenceShort();
        int half = list.size() / 2;
        int odd = list.size() % 2;
        for (int i = 0; i < half; i++) {
            String s1 = list.get(i);
            String s2 = list.get(half + odd + i);
            System.out.println(s1 + "\t" + s2);
        }
        if (odd > 0) System.out.println(list.get(half));
    }

    public void printReferenceLong() {
        final String formatRef = "%-6s  %-10s  %-16s  %s";
        for (Mnemonic m : sortByKindName()) {
            if (m.isExtended()) continue;
            System.out.println(String.format(formatRef, m.name, m.format.hint(), m.hint, m.desc));
        }
    }

    public void initMnemonics() {
        // Directives
        put("START",   Opcode.START,  Format.De, "directive", "Define program name and start address.");
        put("END",     Opcode.END,    Format.De, "directive", "End of program.");

        put("CSECT",   Opcode.CSECT,  Format.D,  "directive", "Declare section.");
        put("USE",     Opcode.USE,    Format.Ds0, "directive", "Block entry.");
        put("EXTREF",  Opcode.EXTREF, Format.Ds_, "directive", "Import symbols");
        put("EXTDEF",  Opcode.EXTDEF, Format.Ds_, "directive", "Export symbols");

        put("ORG",     Opcode.ORG,    Format.De0, "directive", "Set location counter.");
        put("LTORG",   Opcode.LTORG,  Format.D, "directive", "Flush literals.");

        put("BASE",    Opcode.BASE,   Format.De, "directive", "Set base register.");
        put("NOBASE",  Opcode.NOBASE, Format.D, "directive", "Unset base register.");

        put("EQU",     Opcode.EQU,    Format.De, "directive", "Equate symbol to expression.");
        // Storage directives
        put("RESB",    Opcode.RESB,  Format.Se,  "storage", "Reserve n bytes.");
        put("RESW",    Opcode.RESW,  Format.Se,  "storage", "Reserve n words.");
        put("RESF",    Opcode.RESF,  Format.Se,  "storage", "Reserve n floats.");
        put("BYTE",    Opcode.BYTE,  Format.Sd,  "storage", "Initialize bytes.");
        put("WORD",    Opcode.WORD,  Format.Sd,  "storage", "Initialize words.");
        put("FLOT",    Opcode.FLOT,  Format.Sd,  "storage", "Initialize floats.");
        // Format 1 map, no operand
        put("FIX",     Opcode.FIX,   Format.F1, "A<-int(F)",    "Convert to fixed point number.");
        put("FLOAT",   Opcode.FLOAT, Format.F1, "F<-float(A)",  "Convert to floating point number.");
        put("NORM",    Opcode.NORM,  Format.F1, "F<-norm(F)",   "Normalize.");
        put("SIO",     Opcode.SIO,   Format.F1, "Start S, A",   "Start program S of I/O channel A.");
        put("HIO",     Opcode.HIO,   Format.F1, "Halt A",       "Halt IO channel (A).");
        put("TIO",     Opcode.TIO,   Format.F1, "Test A",       "Test IO channel (A).");
        // Format 2 map, one or two operands
        put("SVC",     Opcode.SVC,    Format.F2n, "Interrupt n", "Generate SVC interrupt n.");
        put("CLEAR",   Opcode.CLEAR,  Format.F2r, "r<-0", "Clear register.");
        put("TIXR",    Opcode.TIXR,   Format.F2r, "X<-(X)+1;(X):(r)", "Increment and compare index register.");
        put("SHIFTL",  Opcode.SHIFTL, Format.F2rn, "(r)<-(r)<<n", "Shift left n bits");
        put("SHIFTR",  Opcode.SHIFTR, Format.F2rn, "(r)<-(r)>>n", "Shift right n bits");
        put("ADDR",    Opcode.ADDR,   Format.F2rr, "r2<-(r2)+(r1)", "Add registers");
        put("SUBR",    Opcode.SUBR,   Format.F2rr, "r2<-(r2)-(r1)", "Subtract registers");
        put("MULR",    Opcode.MULR,   Format.F2rr,  "r2<-(r2)*(r1)", "Multiply registers");
        put("DIVR",    Opcode.DIVR,   Format.F2rr, "r2<-(r2)/(r1)", "Divide registers");
        put("COMPR",   Opcode.COMPR,  Format.F2rr,  "(r1):(r2)", "Compare registers");
        put("RMO",     Opcode.RMO,    Format.F2rr, "(r2)<-(r1)", "Move register");
        // Load and store
        put34("LDA",   Opcode.LDA, "A<-(m..m+2)", "Load register A from address m");
        put34("LDCH",  Opcode.LDCH, "A.1<-(m)", "Load byte to register A from address m");
        put34("LDB",   Opcode.LDB, "B<-(m..m+2)", "Load register B from address m");
        put34("LDF",   Opcode.LDF, "F<-(m..m+5)", "Load register F from address m");
        put34("LDL",   Opcode.LDL, "L<-(m..m+2)", "Load register L from address m");
        put34("LDS",   Opcode.LDS, "S<-(m..m+2)", "Load register S from address m");
        put34("LDT",   Opcode.LDT, "T<-(m..m+2)", "Load register T from address m");
        put34("LDX",   Opcode.LDX, "X<-(m..m+2)", "Load register X from address m");
        put34("LPS",   Opcode.LPS, "PS->(m..2)", "Load processor status from address m");
        put34("STA",   Opcode.STA, "m..m+2<-(A)", "Store register A to address m");
        put34("STCH",  Opcode.STCH, "m<-(A.1)", "Store byte from register A to address m");
        put34("STB",   Opcode.STB, "m..m+2<-(B)", "Store register B to address m");
        put34("STF",   Opcode.STF, "m..m+5<-(F)", "Store register F to address m");
        put34("STL",   Opcode.STL, "m..m+2<-(L)", "Store register L to address m");
        put34("STS",   Opcode.STS, "m..m+2<-(S)", "Store register S to address m");
        put34("STT",   Opcode.STT, "m..m+2<-(T)", "Store register T to address m");
        put34("STX",   Opcode.STX, "m..m+2<-(X)", "Store register X to address m");
        put34("STI",   Opcode.STI, "timer<-(m..m+2)", "Set interval timer");
        put34("STSW",  Opcode.STSW, "m..m+2<-(SW)", "Store processor status word to address m");
        // fixed point operations, register-memory
        put34("ADD",   Opcode.ADD, "A<-(A)+(m..m+2)", "Add to accumulator");
        put34("SUB",   Opcode.SUB, "A<-(A)-(m..m+2)", "Subtract from accumulator");
        put34("MUL",   Opcode.MUL, "A<-(A)*(m..m+2)", "Multiply with accumultator");
        put34("DIV",   Opcode.DIV, "A<-(A)/(m..m+2)", "Divide accumulator");
        put34("COMP",  Opcode.COMP, "A<-(A):(m..m+2)", "Compare accumulator");
        put34("AND",   Opcode.AND, "A<-(A)&(m..m+2)", "Bitwise and accumulator");
        put34("OR",	   Opcode.OR, "A<-(A)|(m..m+2)", "Bitwise or accumulator");
        put34("TIX",   Opcode.TIX, "X<-(X)+1;(X):(m..m+2)", "Increment and compare index register");
        // floating point arithmetic
        put34("ADDF",  Opcode.ADDF, "F<-(F)+(m..m+2)", "Floating point addition");
        put34("SUBF",  Opcode.SUBF, "F<-(F)-(m..m+2)", "Floating point subtraction");
        put34("MULF",  Opcode.MULF, "F<-(F)*(m..m+2)", "Floating point multiplication");
        put34("DIVF",  Opcode.DIVF, "F<-(F)/(m..m+2)", "Floating point division");
        put34("COMPF", Opcode.COMPF, "F<-(F):(m..m+5)", "Floating point comparison");
        // jumps
        put34("J",     Opcode.J, "PC<-m\t", "Unconditional jump");
        put34("JEQ",   Opcode.JEQ, "PC<-m if CC is =", "Jump if equal");
        put34("JGT",   Opcode.JGT, "PC<-m if CC is >", "Jump if greater than");
        put34("JLT",   Opcode.JLT, "PC<-m if CC is <", "Jump if lower than");
        put34("JSUB",  Opcode.JSUB, "L<-(PC);PC<-m", "Jump to subrutine");
        put("RSUB",    Opcode.RSUB, Format.F3, "PC<-(L)", "Return from subroutine.");
        // IO
        put34("RD",	   Opcode.RD, "A.1<-readdev (m)", "Read from device");
        put34("WD",	   Opcode.WD, "writedev(m),A.1", "Write to device");
        put34("TD",	   Opcode.TD, "testdev(m)", "Test device");
        // System
        put34("SSK",   Opcode.SSK, "m<-(A)\t", "Protection key for address");
    }

}
