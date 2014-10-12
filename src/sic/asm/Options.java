package sic.asm;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Options {

    // parser
    public static boolean requireWhitespace = true;   // after label, after mnemonic
    public static boolean requireCommentDot = false;  // comment start with dot | automatic comment after command
    public static boolean skipEmptyLines = false;     // advance empty commands | empty commands as comments

    // obj generator
    public static boolean addSpaceInObj = false;       // add space between fields in obj files

}
