package sic.common;

/**
 * Various conversions from SIC/XE to Java/Hex/...
 * @author jure
 */
public class Conversion {

    // ************ sic <-> hex conversions

    public static int hexToInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text, 16);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int hexToInt(String text) {
        return hexToInt(text, 0);
    }

    public static String addrToHex(int value) {
        return String.format("%05X", value & 0xFFFFF);
    }

    public static String wordToHex(int value) {
        return String.format("%06X", value & 0xFFFFFF);
    }

    public static String byteToHex(int value) {
        return String.format("%02X", value & 0xFF);
    }

    public static String floatToHex(double value) {
        return String.format("%012X", SICXE.floatToBits(value));
    }

    public static byte[] hexToBytes(String str, int multiple) {
        assert str.length() % 2 == 0;
        int len = str.length() / 2;
        len = (len + multiple - 1) / multiple * multiple;  // round up
        byte[] data = new byte[len];
        for (int i = 0, j = 0; i < data.length; i++, j += 2)
            data[i] = (byte) Integer.parseInt(str.substring(j, j + 2), 16);
        return data;
    }

    public static byte[] hexToBytes(String str) {
        return hexToBytes(str, 1);
    }

    public static String bytesToHex(byte[] data, int pos, int len) {
        StringBuilder b = new StringBuilder();
        for (int i = pos; i < pos + len && i < data.length; i++)
            b.append(String.format("%02X", data[i]));
        return b.toString();
    }

    public static String bytesToHex(byte[] data) {
        return bytesToHex(data, 0, data.length);
    }

    public static String bytesToHexNice(byte[] data, int limit) {
        if (data.length > limit)
            return String.format("%02X....%02X", data[0], data[data.length - 1]);
        else
            return String.format("%-8s", Conversion.bytesToHex(data, 0, data.length));
    }

    public static String hexDump(byte[] data, int pos, int rows) {
        StringBuilder b = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            b.append(String.format("%05X: ", pos));
            ascii.setLength(0);
            for (int j = 0; j <= 0xF; j++) {
                byte ch = data[pos++];
                b.append(String.format("%02X ", ch));
                if (ch < 0x20 || ch > 0x7E)
                    ch = '.';
                ascii.append((char)ch);
            }
            b.append(ascii);
            b.append("\n");
        }
        return b.toString();
    }

    // ************ sic -> bin conversion

    public static String byteToBin(int value) {
        String s = Integer.toBinaryString(value & 0xFF);
        while (s.length() % 8 != 0) s = "0" + s;
        return s;
    }


    // ************ initalized data to string

    // initalized data to 1,2,3 (byte)
    public static String dataToByte(byte[] data) {
        StringBuilder buf = new StringBuilder(3 * data.length);
        for (int i = 0; i < data.length - 1; i++) {
            buf.append(Integer.toString(((int) data[i]) & 0xFF));
            buf.append(',');
        }
        buf.append(Integer.toString(((int)data[data.length - 1]) & 0xFF));
        return buf.toString();
    }

    // initalized data to 1,2,3 (word)
    public static String dataToWord(byte[] data) {
        StringBuilder buf = new StringBuilder(3 * data.length);
        int i = 0;
        while (i < data.length - 2) {
            int num = (((int)data[i]) << 16) | (((int)data[i+1]) << 8) | data[i+2];
            buf.append(Integer.toString(num));
            if (i < data.length - 3) buf.append(',');
            i += 3;
        }
        return buf.toString();
    }

    // initalized data to 1,2,3 (float)
    public static String dataToFloat(byte[] data) {
        StringBuilder buf = new StringBuilder(3 * data.length);
        int i = 0;
        while (i < data.length - 5) {
            long num = (((long)data[i]) << 40) | (((long)data[i+1]) << 32) | (((long)data[i+2]) << 24) |
                      (((long)data[i+3]) << 16) | (((long)data[i+4]) << 8) | data[i+5];
            buf.append(SICXE.bitsToFloat(num));
            if (i < data.length - 6) buf.append(',');
            i += 6;
        }
        return buf.toString();
    }

    // ************ register index <-> name

    public static String regToName(int i) {
        final String[] regs = {"A", "X", "L", "B", "S", "T", "F" };
        try {
            return regs[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "?";
        }
    }

    public static int nameToReg(char name) {
        return "AXLBSTF".indexOf(name);
    }

    // ************ Various ***********

    public static String bytesToStr(byte[] data, int pos, int len) {
        StringBuilder b = new StringBuilder();
        for (int i = pos; i < pos + len && i < data.length; i++)
            b.append(String.format("%02X", data[i]));
        return b.toString();
    }

}
