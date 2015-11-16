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
public class GraphicalScreen {
    public final int ADDRESS = 0xA000;
    public final int COLS = 64;
    public final int ROWS = 64;
    public final int PIXELSIZE = 4;

    private final Memory memory;
    // settings
    private int address;
    private int rows;
    private int cols;
    private int pixelSize;
    // gui
    private final JFrame view;
    private JPanel pnlScreen;

    public GraphicalScreen(final Executor executor) {
        this.memory = executor.getMachine().memory;
        this.view = createView();
        setScreen(ADDRESS, COLS, ROWS, PIXELSIZE);
    }

    private JFrame createView() {
        pnlScreen = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintScreen(g);
            }
        };
        pnlScreen.setToolTipText("Double click to clear screen.");
        pnlScreen.addMouseListener(new MouseAdapter() {
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
        bevel.setLayout(new BorderLayout());
        bevel.add(pnlScreen);

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

    void setScreen(int addr, int cols, int rows, int pixelSize) {
        int maxaddr = SICXE.MASK_ADDR - cols * rows;
        if (addr > maxaddr) addr = maxaddr;
        this.address = addr;
        this.rows = rows;
        this.cols = cols;
        this.pixelSize = pixelSize;
        pnlScreen.setPreferredSize(new Dimension(cols * pixelSize, rows * pixelSize));
        updateView();
        view.pack();
    }

    public void toggleView() {
        view.setVisible(!view.isVisible());
    }

    public void updateView() {
        pnlScreen.repaint();
    }

    void paintScreen(Graphics g) {
        if (memory == null) return;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                int color = memory.getByte(address + i * cols + j);
                int amp = (((color >> 6) & 3) + 1) * 20;
                int red = ((color >> 4) & 3) * amp;
                int green = ((color >> 2) & 3) * amp;
                int blue = (color & 3) * amp;
                g.setColor(new Color(red, green, blue));
                g.fillRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
        }
    }

    public void clearScreen() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                memory.setByte(address + i * cols + j, ' ');
    }

    public JPanel createSettingsPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(4, 2, 0, 0));

        final JTextField txtAddr = GUI.createField(pane, "Address", Conversion.addrToHex(address), 10);
        final JTextField txtCols = GUI.createField(pane, "Columns", Integer.toString(cols), 10);
        final JTextField txtRows = GUI.createField(pane, "Rows", Integer.toString(rows), 10);
        final JTextField txtFontSize = GUI.createField(pane, "Pixel size", Integer.toString(pixelSize), 10);

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int addr, cols, rows, pixs;
                try {
                    addr = SICXE.intToAddr(Conversion.hexToInt(txtAddr.getText()));
                    cols  = Integer.parseInt(txtCols.getText());
                    rows = Integer.parseInt(txtRows.getText());
                    pixs = Integer.parseInt(txtFontSize.getText());
                } catch (NumberFormatException e) {
                    return;
                }
                setScreen(addr, cols, rows, pixs);
            }
        };

        txtAddr.addActionListener(al);
        txtCols.addActionListener(al);
        txtRows.addActionListener(al);
        txtFontSize.addActionListener(al);

        return pane;
    }

}
