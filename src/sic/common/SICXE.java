package sic.common;

/**
 * SIC/XE computer specifications.
 * Conversions between SIC/XE and Java types.
 *
 * @author jure
 */
public class SICXE {

    /* SIC/XE types:
	 * addr (20-bit):           0 .. 0xFFFFF
	 * sicaddr (15-bit):        0 .. 0x7FFF
	 * word (24-bit):           0 .. 0xFFFFFF
	 * sword (signed 24-bit):   -0x800000 .. 0x7FFFFF
	 * disp (12-bit):           0 .. 0xFFF
	 * sdisp (signed 12-bit):   -0x800 ... 0x7FF
	 * float: 48-bit float
	 * data: bytes of data
	 */

    // ************ addresses: SIC/XE 20 bit memory

    public static final int SIZE_MEM = 1 << 20;  // 1 MB
    public static final int MASK_ADDR = 0xFFFFF;
    public static final int MIN_ADDR = 0;
    public static final int MAX_ADDR = SIZE_MEM - 1;

    public static int intToAddr(int val) {
        return val & MASK_ADDR;
    }

    public static int addrToInt(int val) {
        return val;
    }

    public static boolean isAddr(int val) {
        return MIN_ADDR <= val && val <= MAX_ADDR;
    }

    // ************ addresses: SIC 15 bit memory

    public static final int SIZE_SICMEM = 1 << 15;  // 32 kB
    public static final int MASK_SICADDR = 0x7FFF;
    public static final int MIN_SICADDR = 0;
    public static final int MAX_SICADDR = SIZE_SICMEM - 1;

    public static int intToSicAddr(int val) {
        return val & MASK_SICADDR;
    }

    public static int sicAddrToInt(int val) {
        return val;
    }

    public static boolean isSicAddr(int val) {
        return MIN_SICADDR <= val && val <= MAX_SICADDR;
    }

    // ************ words: unsigned and signed

    public static final int MASK_WORD = 0xFFFFFF;
    public static final int MIN_WORD = 0;
    public static final int MAX_WORD = (1 << 24) - 1;

    // if val > MAX_SWORD then returns sword
    public static int intToWord(int val) {
        if (val >= 0) return val & MASK_WORD;
        return ~(-val - 1) & MASK_WORD;
    }

    public static int wordToInt(int val) {
        return val;
    }

    public static boolean isWord(int val) {
        return MIN_WORD <= val && val <= MAX_WORD;
    }

    public static final int MASK_SWORD = 0x7FFFFF;
    public static final int MIN_SWORD = -(1 << 23);
    public static final int MAX_SWORD = (1 << 23) - 1;

    public static int intToSword(int val) {
        return intToWord(val);
    }

    public static int swordToInt(int val) {
        if (val <= MAX_SWORD) return val;
        return -(~val & MASK_SWORD) - 1;
    }

    public static boolean isSword(int val) {
        return MIN_SWORD <= val && val <= MAX_SWORD;
    }

    // ************ displacement: unsigned and signed

    public static final int MASK_DISP = 0xFFF;
    public static final int MIN_DISP = 0;
    public static final int MAX_DISP = (1 << 12) - 1;

    public static int intToDisp(int val) {
        if (val >= 0) return val & MASK_DISP;
        return MAX_DISP + 1 + val;  // ~(-val - 1) & MASK_DISP;
    }

    public static int dispToInt(int val) {
        return val;
    }

    public static boolean isDisp(int val) {
        return MIN_DISP <= val && val <= MAX_DISP;
    }

    public static final int MASK_SDISP = 0x7FF;
    public static final int MIN_SDISP = -(1 << 11);
    public static final int MAX_SDISP = (1 << 11) - 1;

    public static int intToSdisp(int val) {
        return intToDisp(val);
    }

    public static int sdispToInt(int val) {
        if (val <= MAX_SWORD) return val;
        return -(~val & MASK_SWORD) - 1;
    }

    public static boolean isSdisp(int val) {
        return MIN_SDISP <= val && val <= MAX_SDISP;
    }

    public static boolean isCdisp(int val) {
        // combined displacement: signed and unsigned
        return MIN_SDISP <= val && val <= MAX_DISP;
    }

    // ************ floats

    public static long floatToBits(double value) {
        return Double.doubleToLongBits(value) >> 16;
    }

    public static double bitsToFloat(long bits) {
        return Double.longBitsToDouble(bits << 16);
    }

    // ************ byte: unsigned and signed

    public static final int MIN_BYTE = 0;
    public static final int MAX_BYTE = (1 << 8) - 1;

    public static final int MIN_SBYTE = -(1 << 7);
    public static final int MAX_SBYTE = (1 << 7) - 1;

    // ************ data (array of bytes) initializers

    public static byte[] intToDataByte(int val) {
        byte[] data = new byte[1];
        data[0] = (byte)(val & 0xFF);
        return data;
    }

    public static byte[] intToDataWord(int val) {
        byte[] data = new byte[3];
        data[0] = (byte)((val >> 16)  & 0xFF);
        data[1] = (byte)((val >> 8) & 0xFF);
        data[2] = (byte)(val & 0xFF);
        return data;
    }

    public static byte[] doubleToDataFloat(double val) {
        long bits = floatToBits(val);
        byte[] data = new byte[6];
        data[0] = (byte)((bits >> 40)  & 0xFF);
        data[1] = (byte)((bits >> 32) & 0xFF);
        data[2] = (byte)((bits >> 24) & 0xFF);
        data[3] = (byte)((bits >> 16) & 0xFF);
        data[4] = (byte)((bits >> 8) & 0xFF);
        data[5] = (byte)(bits & 0xFF);
        return data;
    }

    // ************ devices
    public static final int DEVICE_COUNT = 256;
    public static final int MIN_DEVICE = 0;
    public static final int MAX_DEVICE = DEVICE_COUNT - 1;
    public static final int DEVICE_STDIN = 0;
    public static final int DEVICE_STDOUT = 1;
    public static final int DEVICE_STDERR = 2;
    public static final int DEVICE_FREE = 3;

}
