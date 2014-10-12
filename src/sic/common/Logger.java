package sic.common;

/**
 * @author: jure
 */
public class Logger {

    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void fmtlog(String fmt, Object... params) {
        log(String.format(fmt, params));
    }

    public static void err(String msg) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        String method = ste[ste.length - 1].getMethodName();
        System.err.println(method + ": " + msg);
    }

    public static void fmterr(String fmt, Object... params) {
        err(String.format(fmt, params));
    }

}
