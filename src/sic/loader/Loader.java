package sic.loader;

import sic.asm.Assembler;
import sic.asm.ErrorCatcher;
import sic.ast.Program;
import sic.common.Logger;
import sic.common.Utils;
import sic.sim.vm.Machine;
import sic.sim.vm.Memory;

import java.io.*;

/**
 * @author: jure
 */
public class Loader {

    public static void skipWhitespace(Reader r) throws IOException {
//TODO:        while (Character.isWhitespace((char)r.read()));
    }

    public static String readString(Reader r, int len) throws IOException {
        skipWhitespace(r);
        StringBuilder buf = new StringBuilder();
        while (len-- > 0) buf.append((char)r.read());
        return buf.toString();
    }

    public static int readByte(Reader r) throws IOException {
        skipWhitespace(r);
        return Integer.parseInt(readString(r, 2), 16);
    }

    public static int readWord(Reader r) throws IOException {
        skipWhitespace(r);
        return Integer.parseInt(readString(r, 6), 16);
    }

    public static void loadRawCode(Machine machine, int address, byte[] code) {
        System.arraycopy(code, 0, machine.memory.memory, address, code.length);
        machine.registers.setPC(address);
    }

    public static boolean loadSection(Machine machine, Reader r) {
        try {
            // header record
            if (r.read() != 'H') return false;
            readString(r, 6);	// name is ignored
            int start = readWord(r);
            int length = readWord(r);
            if (r.read() == '\r') // EOL
                r.read();

            Memory mem = machine.memory;
            // text records
            int ch = r.read();
            while (ch == 'T') {
                int loc = readWord(r);
                int len = readByte(r);
                while (len-- > 0) {
                    if (loc < start || loc >= start + length) return false;
                    byte val = (byte)readByte(r);
                    mem.setByteRaw(loc++, val);
                }
                if (r.read() == '\r') // EOL
                    r.read();
                ch = r.read();
            }

            // modification records
            while (ch == 'M') {
                readWord(r);	// addr
                readByte(r);	// len
                if (r.read() == '\r') // EOL
                    r.read();
                ch = r.read();
            }

            // load end record
            if (ch != 'E') return false;
            machine.registers.setPC(readWord(r));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean loadObj(Machine machine, String filename) {
        try {
            Reader reader = new FileReader(filename);
            Loader.loadSection(machine, reader);
        } catch (FileNotFoundException e1) {
            Logger.fmterr("Error reading file '%s'.", filename);
            return false;
        }
        return true;
    }

    static public void loadAsm(Machine machine, String filename) {
        Assembler assembler = new Assembler();
        ErrorCatcher errorCatcher = assembler.errorCatcher;
        Program program = assembler.assemble(Utils.readFile(filename));
        if (errorCatcher.count() > 0) {
            errorCatcher.print();
            return;
        }
        //
        Writer writer = new StringWriter();
        assembler.generateObj(program, writer, false);
        Reader reader = new StringReader(writer.toString());
        Loader.loadSection(machine, reader);
    }

}
