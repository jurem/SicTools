package sic.common;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class GUI {

    static JFileChooser fc;

    static {
        fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
    }

    public static File openFileDialog(JFrame frame, FileFilter filter) {
        fc.resetChoosableFileFilters();
        fc.setFileFilter(filter);
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile();
        return null;
    }

    public static JFrame showInJFrame(String title, Container container, int x, int y) {
        JFrame frame = new JFrame(title);
        frame.setContentPane(container);
        frame.pack();
        frame.setLocation(x, y);
        frame.setVisible(true);
        return frame;
    }

    public static JMenuItem addMenuItem(JMenu menu, String caption, int mnemonic, ActionListener actionListener) {
        JMenuItem mi = new JMenuItem(caption, mnemonic);
        mi.addActionListener(actionListener);
        menu.add(mi);
        return mi;
    }

    public static JTextField createField(JPanel pane, String label, String text, int columns, ActionListener listener) {
        JLabel lbl = new JLabel(label);
        JTextField txt = new JTextField(text, columns);
        if (listener != null) txt.addActionListener(listener);
        lbl.setLabelFor(txt);
        pane.add(lbl);
        pane.add(txt);
        return txt;
    }

    public static JTextField createField(JPanel pane, String label, String text, int columns) {
        return createField(pane, label, text, columns, null);
    }

}
