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
    // condition code of status word register
    private int CC;     // TODO: full status word support

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
        if (CC == 0) return 0;
        else if (CC < 0) return 0x40;
        else return 0x80;
    }

    public boolean isLower() {
        return CC < 0;
    }

    public boolean isEqual() {
        return CC == 0;
    }

    public boolean isGreater() {
        return CC > 0;
    }

    public void setSW(int value) {
        if ((value & 0x40) == 0x40) CC = -1;
        else if ((value & 0x80) == 0x80) CC = 1;
        else CC = 0;
    }

    public void setSWAfterCompare(int compare) {
        CC = compare;
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
        CC = 0;
    }

    public Registers() {
        reset();
    }

}
