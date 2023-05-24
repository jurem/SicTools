package sic.sim.addons.graph;

import sic.common.Conversion;
import sic.common.GUI;
import sic.common.SICXE;
import sic.sim.Executor;
import sic.sim.addons.Addon;
import sic.sim.vm.Memory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TimerTask;
import java.util.Vector;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class GraphicalScreen extends Addon {
    public final int ADDRESS = 0xA000;
    public final int COLS = 64;
    public final int ROWS = 64;
    public final int PIXELSIZE = 4;

    private Memory memory;
    // settings
    private int address = ADDRESS;
    private int rows = COLS;
    private int cols = ROWS;
    private int pixelSize = PIXELSIZE;
    private int freq = 120;
    // gui
    private JFrame view;
    private JPanel pnlScreen;

    @Override
    public void load(String pars) {
        if (pars != null) {
            int x = pars.indexOf('x');
            int at = pars.indexOf('@');

            String sCols = pars.substring(0, x);
            String sRows;
            String hz;
            if (at != -1) {
                sRows = pars.substring(x + 1, at);
                hz = pars.substring(at + 1);
            } else {
                sRows = pars.substring(x + 1);
                hz = "120";
            }

            cols = Integer.parseInt(sCols);
            rows = Integer.parseInt(sRows);
            freq = Integer.parseInt(hz);
        }
    }

    @Override
    public void init(Executor executor) {
        this.memory = executor.getMachine().memory;
        this.view = createView();
        setScreen(address, cols, rows, pixelSize);
        //setSize(arg.getGraphScrCols(), arg.getGraphScrRows());
        toggleView();
    }

    @Override
    public Vector<Timer> getTimers() {
        Vector<Timer> ts = new Vector<Timer>();
        // Calculate graphical screen refresh rate
        double specifiedMs = 1000.0 / (double) (freq <= 0 ? 120 : freq);
        long refreshMs = (long) Math.max(Math.floor(specifiedMs), 4); // Cap at 240 Hz
        ts.add(new Timer(new TimerTask() {
            @Override
            public void run() {
                updateView();
            }
        }, refreshMs));
        return ts;
    }

    @Override
    public Vector<MenuEntry> getMenuEntries() {
        Vector<MenuEntry> es = new Vector<MenuEntry>();
        es.add(new MenuEntry("Toggle graphical screen", KeyEvent.VK_G, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                toggleView();
            }
        }));
        return es;
    }

    @Override
    public Vector<SettingsPanel> getSettingsPanels() {
        Vector<SettingsPanel> panels = new Vector<SettingsPanel>();
        panels.add(new SettingsPanel("Graphical screen", createSettingsPane()));
        return panels;
    }

    /////////////// screen view
    void paintScreen(Graphics g) {
        if (memory == null) return;

        // Start rendering; set VSync to 0
        memory.setByteRaw(address + rows * cols, 0);

        // Render screen
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int color = memory.getByteRaw(address + i * cols + j);
                int amp = (((color >> 6) & 3) + 1) * 20;
                int red = ((color >> 4) & 3) * amp;
                int green = ((color >> 2) & 3) * amp;
                int blue = (color & 3) * amp;
                g.setColor(new Color(red, green, blue));
                g.fillRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
            }
        }

        // After rendering; set VSync to 1
        memory.setByteRaw(address + rows * cols, 1);
    }

    public void clearScreen() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                memory.setByteRaw(address + i * cols + j, 0);
    }

    private JFrame createView() {
        final JFrame frame = new JFrame("Graphical screen");
        frame.setResizable(false);

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
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPanel panel = createSettingsPane();
                    GUI.showInJFrame(frame, "Settings", panel);

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

        frame.setContentPane(panel);
        return frame;
    }

    public void setSize(int cols, int rows) {
        setScreen(address, cols, rows, pixelSize);
    }

    public void setScreen(int addr, int cols, int rows, int pixelSize) {
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



    /////////////// settings

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
