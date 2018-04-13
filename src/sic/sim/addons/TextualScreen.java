package sic.sim.addons;

import sic.common.Conversion;
import sic.common.GUI;
import sic.common.SICXE;
import sic.sim.Executor;
import sic.sim.vm.Memory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class TextualScreen {
    public final int ADDRESS = 0xB800;
    public final int COLS = 80;
    public final int ROWS = 25;
    public final int FONTSIZE = 12;

    private final Memory memory;
    // settings
    private int address;
    private int rows;
    private int cols;
    // gui
    private final JFrame view;
    private JTextArea txtScreen;

    public TextualScreen(final Executor executor) {
        this.memory = executor.getMachine().memory;
        this.view = createView();
        setScreen(ADDRESS, COLS, ROWS, FONTSIZE);
    }

    public void clearScreen() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                memory.setByteRaw(address + i * cols + j, ' ');
    }

    public void setSize(int cols, int rows) {
        setScreen(address, cols, rows, txtScreen.getFont().getSize());
    }

    public void setScreen(int addr, int cols, int rows, int fontSize) {
        int maxaddr = SICXE.MASK_ADDR - cols * rows;
        if (addr > maxaddr) addr = maxaddr;
        this.address = addr;
        this.rows = rows;
        this.cols = cols;
        txtScreen.setRows(rows);
        txtScreen.setColumns(cols);
//        txtScreen.setFont(new java.awt.Font("Courier New", java.awt.Font.BOLD, fontSize));
        txtScreen.setFont(new Font("monospaced", Font.BOLD, fontSize));
        updateView();
        view.pack();
    }

    private JFrame createView() {
        txtScreen = new JTextArea();
        txtScreen.setEditable(false);
        txtScreen.setToolTipText("Double click to clear screen.");
        txtScreen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    clearScreen();
                    updateView();
                }
            }
        });

        JPanel bevel = new JPanel();
        bevel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        bevel.setLayout(new java.awt.BorderLayout());
        bevel.add(txtScreen);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout());
        panel.add(bevel);

        JFrame frame = new JFrame("Screen");
        frame.setResizable(false);
//        frame.setBounds(620, 370, 500, 300);
        frame.setContentPane(panel);

        return frame;
    }

    public void updateView() {
        if (memory == null) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char znak = (char) memory.getByteRaw(address + i * cols + j);
                if (znak > 31) sb.append(znak); else sb.append(" ");
            }
            if (i < rows-1) sb.append("\n");
        }
        txtScreen.setText(sb.toString());
    }

    public void toggleView() {
        view.setVisible(!view.isVisible());
    }

    public JPanel createSettingsPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(4, 2, 0, 0));

        final JTextField txtAddr = GUI.createField(pane, "Address", Conversion.addrToHex(address), 10);
        final JTextField txtCols = GUI.createField(pane, "Columns", Integer.toString(cols), 10);
        final JTextField txtRows = GUI.createField(pane, "Rows", Integer.toString(rows), 10);
        final JTextField txtFontSize = GUI.createField(pane, "Font size", Integer.toString(txtScreen.getFont().getSize()), 10);

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int addr, cols, rows, fontsize;
                try {
                    addr = SICXE.intToAddr(Conversion.hexToInt(txtAddr.getText()));
                    cols  = Integer.parseInt(txtCols.getText());
                    rows = Integer.parseInt(txtRows.getText());
                    fontsize = Integer.parseInt(txtFontSize.getText());
                } catch (NumberFormatException e) {
                    return;
                }
                setScreen(addr, cols, rows, fontsize);
            }
        };

        txtAddr.addActionListener(al);
        txtCols.addActionListener(al);
        txtRows.addActionListener(al);
        txtFontSize.addActionListener(al);

        return pane;
    }

}
