package sic.sim.vm;

import sic.common.Conversion;
import sic.common.Logger;
import sic.common.SICXE;
import sic.sim.breakpoints.DataBreakpoints;
import sic.sim.breakpoints.ReadDataBreakpointException;
import sic.sim.breakpoints.WriteDataBreakpointException;

import java.util.Arrays;

/**
 * Memory
 * @author jure
 */
public class Memory {

    public final byte[] memory;

    public DataBreakpoints dataBreakpoints = new DataBreakpoints();

    public Memory(int capacity) {
        this.memory = new byte[capacity];
    }

    public void reset() {
        Arrays.fill(memory, (byte)0);
    }
    /**
     * Checks if the address is inside memory bounds
     * @return true if address is invalid
     */
    private boolean checkAddress(int address) {
        boolean invalid = address < 0 || address >= memory.length;
        if (invalid)
            Logger.fmterr("Invalid memory address '%s', %d", Conversion.addrToHex(address), address);
        return invalid;
    }

    // ----------------------------------------------
    // Memory access methods that trigger breakpoints
    // ----------------------------------------------

    public int getByte(int address) throws ReadDataBreakpointException {
        if (checkAddress(address)) return 0;
        dataBreakpoints.checkRead(address);
        return ((int)memory[address]) & 0xFF;
    }

    public void setByte(int address, int value) throws WriteDataBreakpointException {
        if (checkAddress(address)) return;
        dataBreakpoints.checkWrite(address);
        memory[address] = (byte)(value & 0xFF);
    }

    public int getWord(int address) throws ReadDataBreakpointException {
        return getByte(address + 2) | getByte(address + 1) << 8 | getByte(address) << 16;
    }

    public void setWord(int address, int value) throws WriteDataBreakpointException {
        setByte(address, value >> 16);
        setByte(address + 1, value >> 8);
        setByte(address + 2, value);
    }

    public double getFloat(int address) throws ReadDataBreakpointException {
        long bits  =  (long)getByte(address)  << 40 | (long)getByte(address+1) << 32 |
                      (long)getByte(address+2) << 24 | getByte(address+3) << 16 |
                      getByte(address+4) << 8  | getByte(address+5);
        return SICXE.bitsToFloat(bits);
    }

    public void setFloat(int address, double value) throws WriteDataBreakpointException {
        long bits = SICXE.floatToBits(value);
        setByte(address, (int)(bits >> 40));
        setByte(address + 1, (int)(bits >> 32));
        setByte(address + 2, (int)(bits >> 24));
        setByte(address + 3, (int)(bits >> 16));
        setByte(address + 4, (int)(bits >> 8));
        setByte(address + 5, (int)(bits));
    }

    // ----------------------------------------------------
    // Memory access methods that DON'T trigger breakpoints
    // ----------------------------------------------------
    // Useful for the user parts of simulator, like:
    // - screens
    // - watches

    public int getByteRaw(int address) {
        if (checkAddress(address)) return 0;
        return ((int)memory[address]) & 0xFF;
    }

    public void setByteRaw(int address, int value) {
        if (checkAddress(address)) return;
        memory[address] = (byte)(value & 0xFF);
    }

    public int getWordRaw(int address) {
        return getByteRaw(address + 2) | getByteRaw(address + 1) << 8 | getByteRaw(address) << 16;
    }

    public void setWordRaw(int address, int value) {
        setByteRaw(address, value >> 16);
        setByteRaw(address + 1, value >> 8);
        setByteRaw(address + 2, value);
    }

    public double getFloatRaw(int address) {
        long bits = (long)getByteRaw(address)  << 40 | (long)getByteRaw(address+1) << 32 |
                (long)getByteRaw(address+2) << 24 | getByteRaw(address+3) << 16 |
                getByteRaw(address+4) << 8  | getByteRaw(address+5);
        return SICXE.bitsToFloat(bits);
    }

    public void setFloatRaw(int address, double value) {
        long bits = SICXE.floatToBits(value);
        setByteRaw(address, (int)(bits >> 40));
        setByteRaw(address + 1, (int)(bits >> 32));
        setByteRaw(address + 2, (int)(bits >> 24));
        setByteRaw(address + 3, (int)(bits >> 16));
        setByteRaw(address + 4, (int)(bits >> 8));
        setByteRaw(address + 5, (int)(bits));
    }

}
