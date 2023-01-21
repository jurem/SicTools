package sic.asm.parsing;

import sic.asm.AsmError;
import sic.common.Conversion;

/**
 *  * Lexer - source code tokenization
 *
 * @author jure
 */
public class Lexer extends Input {

    // ************ isXXX()

    public boolean isWhitespace() {
        return peek() == ' ' || peek() == '\t' || peek() == '\r';
    }

    public boolean isAlpha() {
        return Character.isLetter(peek()) || peek() == '_';
    }

    public boolean isAlphanumeric() {
        return Character.isLetterOrDigit(peek()) || peek() == '_';
    }

    public boolean isNewLine() {
        return peek() == '\n';
    }

    // ************ advance and read

    public void skipWhitespace() {
        while (isWhitespace()) advance();
    }

    public String skipLinesAndComments() {
        StringBuilder comments = new StringBuilder();
        while (isWhitespace() || isNewLine() || peek() == '.') {
            String comment = readIfComment(true, true);
            if (comment != null) {
                comments.append(comment);
                comments.append('\n');
            } else {
                advance();
            }
        }
        return comments.length() > 0 ? comments.toString() : null;
    }

    public void skipAlphanumeric() {
        while (isAlphanumeric()) advance();
    }

    public String readAlphanumeric() {
        int mark = pos;
        skipAlphanumeric();
        return extract(mark);
    }

    public String readDigits(int radix) {
        int mark = pos;
        while (Character.digit(peek(), radix) != -1) advance();
        return extract(mark);
    }

    // ************ SIC/XE if-tokens

    public String readIfComment(boolean requireDot, boolean skipEmptyLines) {
        boolean hasDot = advanceIf('.');
        if ((requireDot || col == 1) && !hasDot) return null;
        String comment = readUntil('\n').trim();
        if (skipEmptyLines && comment.length() == 0) return null;
        return comment;
    }

    public String readIfLabel() {
        if (col == 1 && isAlpha()) return readAlphanumeric();
        return null;
    }

    public String readIfMnemonic() {
        int mark = pos;
        advanceIf('+');
        skipAlphanumeric();
        if (mark == pos) return null;
        return extract(mark);
    }

    // ************ SIC/XE tokens

    public void skipComma() throws AsmError {
         skipWhitespace();
         advance(',');
         skipWhitespace();
    }

    public boolean skipIfComma() {
         skipWhitespace();
         boolean res = advanceIf(',');
         skipWhitespace();
         return res;
    }

    public boolean skipIfIndexed() throws AsmError {
        if (skipIfComma()) {
            advance('X');
            return true;
        }
        return false;
    }

    public int readRegister() throws AsmError {
         char ch = advance();
         int reg = Conversion.nameToReg(ch);
         if (reg < 0)
             throw new AsmError(loc(), "Invalid register '%c'", ch);
         return reg;
     }

    public String readSymbol() throws AsmError {
        String sym = readAlphanumeric();
        if (sym.length() > 0) return sym;
        throw new AsmError(loc(), "Symbol expected");
    }

    public String readIfSymbol() {
        return readAlphanumeric();
    }

    /**
      * Parse integer in any of the available formats.
      * Formats: standard, 0bBIN, 0oOCT, 0xHEX.
      * If minus sign is present it must be immediately followed by the number.
      * @param lo ... lower limit
      * @param hi ... upper limit
      * @return parsed integer number
      * @throws sic.asm.AsmError
      */
    public int readInt(int lo, int hi) throws AsmError {
        // first detect radix
        int radix = -1;
        boolean negative = advanceIf('-');
        if (peek() == '0') {
            // 0bBIN, 0oOCT, 0xHEX
            switch (peek(1)) {
                case 'b': radix = 2; break;
                case 'o': radix = 8; break;
                case 'x': radix = 16; break;
            }
            // we got radix != 10
            if (radix != -1) {
                advance();
                advance();
            } else radix = 10;
        } else if (Character.isDigit(peek()))
            radix = 10;
        else
            throw new AsmError(loc(), "Number expected");
        // read digits
        int num;
        try {
            num = Integer.parseInt(readDigits(radix), radix);
        } catch (NumberFormatException e) {
            throw new AsmError(loc(), "Invalid number");
        }
        // number must not be followed by letter or digit
        if (Character.isLetterOrDigit(peek()))
            throw new AsmError(loc(), "invalid digit '%c'", peek());
        // check range
        if (negative) num = -num;
        if (num < lo || num > hi)
            throw new AsmError(loc(), "Number '%d' out of range [%d..%d]", num, lo, hi);
        return num;
    }

    /**
     * Parse a 48-bit double.
     * If minus sign is present it must be immediately followed by the number.
     * @return parsed double
     * @throws sic.asm.AsmError
     */
    public double readFloat() throws AsmError {
        // Check sign
        boolean negative = advanceIf('-');

        // Read numbers before dot
        double num;
        try {
            num = Double.parseDouble(readDigits(10));
        } catch (NumberFormatException e) {
            throw new AsmError(loc(), "Invalid number");
        }

        // Check for dot
        if (advanceIf('.')) {
            try {
                num += Double.parseDouble("0." + readDigits(10));
            } catch (NumberFormatException e) {
                throw new AsmError(loc(), "Invalid number");
            }
        }

        // Number must not be followed by letter or digit
        if (Character.isLetterOrDigit(peek()))
            throw new AsmError(loc(), "invalid digit '%c'", peek());

        // Apply sign
        if (negative) num = -num;

        // Check range
        double sicDoubleLimit = Math.pow(2, 11 + 36) - 1;
        double lo = -sicDoubleLimit;
        double hi = sicDoubleLimit;
        if (num < lo || num > hi)
            throw new AsmError(loc(), "Number '%d' out of range [%d..%d]", num, lo, hi);

        return num;
    }

    public String readEscapedString(char terminator) throws AsmError {
        StringBuilder buf = new StringBuilder();
        for (char c = advance(); c != terminator; c = advance()) {
            if (!ready() || c == '\n') {
                throw new AsmError(loc(), "Unterminated byte string");
            }
            if (c == '\\') {
                c = advance();
                switch (c) {
                    case '\"':
                    case '\\':
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 't':
                        c = '\t';
                        break;
                    case 'b':
                        c = '\b';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    case '0':
                        c = '\0';
                        break;
                    case 'x':
                        int mark = pos();
                        advance(2);
                        String str = extract(mark);
                        try {
                            c = (char) Integer.parseInt(str, 16);
                        } catch (NumberFormatException e) {
                            throw new AsmError(loc(), "Hexadecimal byte expected");
                        }
                        break;
                    default:
                        throw new AsmError(loc(), "Unknown escape sequence '\\%c'", c);
                }
            }
            buf.append(c);
        }
        return buf.toString();
    }

}
