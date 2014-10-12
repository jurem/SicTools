package sic.common;

import java.io.*;

/**
 * Various utility functions.
 *
 * @author jure
 */
public class Utils {

    public static boolean isInInterval(int n, int lo, int hi) {
        return lo <= n && n <= hi;
    }

    public static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) return filename.substring(lastDot + 1);
        return "";
    }

    public static String getFileBasename(String filename) {
        int lastDot = filename.lastIndexOf(".");
        return lastDot != -1 ? filename.substring(0, lastDot) : filename;
    }

    // ************ reading

    public static String readString(Reader r, int len) throws IOException {
        StringBuilder buf = new StringBuilder();
        while (len-- > 0) buf.append((char)r.read());
        return buf.toString();
    }

    public static int readByte(Reader r) throws IOException {
        return Integer.parseInt(readString(r, 2), 16);
    }

    public static int readWord(Reader r) throws IOException {
        return Integer.parseInt(readString(r, 6), 16);
    }

    public static String readFile(File file) {
        byte[] buf = new byte[(int) file.length()];
        try {
            InputStream s = new FileInputStream(file);
            try {
                s.read(buf);
            } finally {
                s.close();
            }
        } catch (IOException e) {
            Logger.fmterr("Error reading file '%s'.", file.getName());
            System.exit(1);
        }
        return new String(buf);
    }

    public static String readFile(String filename) {
        return readFile(new File(filename));
    }

    public static String readStdin() {
        try {
            StringBuilder buf = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String l;
            while ((l = in.readLine()) != null)
                buf.append(l).append("\n");
            return buf.toString();
        } catch (IOException e) {
            return "";
        }
    }

    // ************ writing

    public static Writer createFileWriter(String filename) {
        try {
            return new FileWriter(filename);
        } catch (IOException e) {
            Logger.fmterr("Error creating file '%s'", filename);
            System.exit(1);
            return null;
        }
    }

}
