package sic.sim;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Colors {

    // colors
    public static final Color fg;
    public static final Color bg;
    public static final Color selectionFg;
    public static final Color selectionBg;
    public static final Color selectionInactiveFg;
    public static final Color selectionInactiveBg;

    static {
        UIDefaults defaults = javax.swing.UIManager.getDefaults();
        bg = defaults.getColor("TextField.background");
        fg = defaults.getColor("TextField.foreground");
        selectionBg = defaults.getColor("TextField.selectionBackground");
        selectionFg = defaults.getColor("TextField.selectionForeground");
        selectionInactiveBg = defaults.getColor("TextComponent.selectionBackgroundInactive");
        selectionInactiveFg = Color.BLACK;  //defaults.getColor("TextField.inactiveForeground");
    }

    public static Color fg(boolean selected, boolean active) {
        return (selected ? (active ? selectionFg : selectionInactiveFg) : fg);
    }

    public static Color bg(boolean selected, boolean active) {
        return (selected ? (active ? selectionBg : selectionInactiveBg) : bg);
    }

}
