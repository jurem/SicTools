package sic.sim.vm;

import sic.common.Logger;
import sic.common.SICXE;

/**
 * Registers of the SIC/XE machine.
 * @author jure
 */
public class Registers {

    // SIC commands counter
    private int PC;
    // SIC 24-bit registers
    private int A, X, L;
    // SIC/XE 24-bit registers
    private int S, T, B;
    // SIC/XE 48-bit float register
    private double F;

    private int timer = 0;

    private class StatusWord {
        // 0 = user, 1 = supervisor
        public int MODE = 1;
        // 0 = running, 1 = idle
        public int IDLE = 0;
        // process identifier
        public int ID = 0;
        // condition code
        public int CC = 0;
        // interrupt mask
        public int MASK = 0;
        // interrupt code
        public int INT_CODE = 0;
    }

    private StatusWord SW = new StatusWord();

    // ***** getters/setters ********************
    // get   ... unsigned
    // get_s ... signed

    public int getPC() {
        return PC;
    }

    public void setPC(int val) {
        PC = SICXE.intToAddr(val);
    }

    public void incPC() {
        if (++PC > Machine.MAX_ADDRESS) {
            // TODO: interrupt?
            Logger.fmterr("PC register overflow.");
            PC = 0;
        }
    }

    public int getA() {
        return A;
    }

    public int getAs() {
        return SICXE.swordToInt(A);
    }

    public void setA(int val) {
        A = SICXE.intToWord(val);
    }

    public int getALo() {
        return A & 0xFF;
    }

    public void setALo(int value) {
        A = A & 0xFFFF00 | value & 0xFF;
    }

    public int getX() {
        return X;
    }

    public int getXs() {
        return SICXE.swordToInt(X);
    }

    public void setX(int val) {
        X = SICXE.intToWord(val);
    }

    public int getL() {
        return L;
    }

    public int getLs() {
        return SICXE.swordToInt(L);
    }

    public void setL(int val) {
        L = SICXE.intToWord(val);
    }

    public int getS() {
        return S;
    }

    public int getSs() {
        return SICXE.swordToInt(S);
    }

    public void setS(int val) {
        S = SICXE.intToWord(val);
    }

    public int getT() {
        return T;
    }

    public int getTs() {
        return SICXE.swordToInt(T);
    }

    public void setT(int val) {
        T = SICXE.intToWord(val);
    }

    public int getB() {
        return B;
    }

    public int getBs() {
        return SICXE.swordToInt(B);
    }

    public void setB(int val) {
        B = SICXE.intToWord(val);
    }

    public double getF() {
        return F;
    }

    public void setF(double val) {
        F = val;
    }

    public int getSW() {
        int value = 0;
        value |= SW.MODE;
        value |= SW.IDLE<<1;
        value |= SW.ID<<2;
        int cc = 0;
        if (SW.CC > 0) {
            cc = 0x1;
        } else if (SW.CC < 0) {
            cc = 0x2;
        }
        value |= cc<<6;
        value |= SW.MASK<<8;
        value |= SW.INT_CODE<<16;
        return value;
    }

    public boolean ccIsLower() {
        return SW.CC < 0;
    }

    public boolean ccIsEqual() {
        return SW.CC == 0;
    }

    public boolean ccIsGreater() {
        return SW.CC > 0;
    }

    public boolean isSupervisor() {
        return SW.MODE == 1;
    }

    public boolean isIdle() {
        return SW.IDLE == 1;
    }

    public int processID() {
        return SW.ID;
    }

    public void setIntCode(int value) {
        SW.INT_CODE = value & 0xff;
    }

    public void setSW(int value) {
        SW.MODE = value&0x1;
        SW.IDLE = (value>>1)&0x1;
        SW.ID = (value>>2)&0xf;
        int cc = (value>>6)&0x3;
        if (cc == 0x1) {
            SW.CC = -1;
        } else if (cc == 0x2) {
            SW.CC = 1;
        } else {
            SW.CC = 0;
        }
        SW.MASK = (value>>8)&0xf;
        SW.INT_CODE = (value>>16)&0xff;
    }

    public String explainSW() {
        int cc = 0;
        if (SW.CC > 0) {
            cc = 0x1;
        } else if (SW.CC < 0) {
            cc = 0x2;
        }
        // I hate java
        String mask = String.format("%s%s%s%s",
                intEnabled(Interrupt.IntClass.SVC)     ? "S" : "-",
                intEnabled(Interrupt.IntClass.PROGRAM) ? "P" : "-",
                intEnabled(Interrupt.IntClass.TIMER)   ? "T" : "-",
                intEnabled(Interrupt.IntClass.IO)      ? "I" : "-");

        return String.format("<b>ICODE:</b> 0x%02x <b>MASK:</b> %s <b>CC:</b> %d<br>"
                + "<b>ID:</b> %d <b>IDLE:</b> %s <b>MODE:</b> <span style='%s'>%s</span>",
                SW.INT_CODE, mask, cc,
                SW.ID, SW.IDLE == 0 ? "R" : "I", SW.MODE == 1 ? "color: red;" : "",
                SW.MODE == 1 ? "S" : "U");
    }

    public void setCC(int compare) {
        SW.CC = compare;
    }

    public boolean intEnabled(Interrupt.IntClass c) {
        return (SW.MASK & c.value) > 0;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void tickTimer() {
        timer--;
    }

    // ***** getter/setter by register index ****

    public static final int rA = 0;
    public static final int rX = 1;
    public static final int rL = 2;
    public static final int rB = 3;
    public static final int rS = 4;
    public static final int rT = 5;
    public static final int rF = 6;
    public static final int rPC = 8;
    public static final int rSW = 9;

    public int get(int idx) {
        switch (idx) {
            case rA: return getA();
            case rX: return getX();
            case rL: return getL();
            case rB: return getB();
            case rS: return getS();
            case rT: return getT();
            case rF: return (int)getF();    // TODO
            case rPC: return getPC();       // TODO
            case rSW: return getSW();       // TODO
            default: Logger.fmterr("Invalid register index '%d'", idx);
        }
        return 0;
    }

    // get signed
    public int gets(int idx) {
        switch (idx) {
            case rA: return getAs();
            case rX: return getXs();
            case rL: return getLs();
            case rB: return getBs();
            case rS: return getSs();
            case rT: return getTs();
            // undocumented
            case rF: return (int)getF();    // TODO
            case rPC: return getPC();       // TODO
            case rSW: return getSW();       // TODO
            default: Logger.fmterr("Invalid register index '%d'", idx);
        }
        return 0;
    }

    public void set(int idx, int value) {
        switch (idx) {
            case rA: setA(value); break;
            case rX: setX(value); break;
            case rL: setL(value); break;
            case rB: setB(value); break;
            case rS: setS(value); break;
            case rT: setT(value); break;
            case rF: setF(value); break;
            case rPC: setPC(value); break;
            case rSW: setSW(value); break;
            default: Logger.fmterr("Invalid register index '%d'", idx);
        }
    }

    // ************ other methods ????
    
    public void reset() {
        PC = 0;
        A = X = L = 0;
        B = S = T = 0;
        F = 0;
        timer = 0;
        SW = new StatusWord();
    }

    public Registers() {
        reset();
    }

}
