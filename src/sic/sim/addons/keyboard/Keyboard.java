package sic.sim.addons.keyboard;

import sic.common.Conversion;
import sic.common.GUI;
import sic.common.SICXE;
import sic.common.Logger;
import sic.sim.Executor;
import sic.sim.vm.Memory;
import sic.sim.addons.Addon;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;


/**
 * Keyboard addon for SicTools
 *
 * Stores character value of latest key press into the memory
 * The Keyboard window has to be opened and focused
 */
public class Keyboard extends Addon {

    public final int ADDRESS = 0xC000;

    private Memory memory;
    // gui
    private JFrame view;
    // settings
    private int address = ADDRESS;
    private JTextArea inputArea;

    @Override
    public void load(String args) {
        if (args != null) {
            address = Integer.decode(args);
        }
    }

    @Override
    public void init(Executor executor) {
        this.memory = executor.getMachine().memory;
        this.view = createView();
        setScreen(address);
        toggleView();
    }

    @Override
    public Vector<MenuEntry> getMenuEntries() {
        Vector<MenuEntry> es = new Vector<MenuEntry>();
        es.add(new MenuEntry("Toggle keyboard", KeyEvent.VK_K, KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK), new ActionListener() {
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
        panels.add(new SettingsPanel("Keyboard", createSettingsPane()));
        return panels;
    }

    public void setSize(int cols, int rows) {
        setScreen(address);
    }

    public void setScreen(int addr) {
        this.address = addr;
        view.pack();
    }

    private JFrame createView() {
        inputArea = new JTextArea();
        inputArea.setEditable(false);
        inputArea.setText("Type while this window is focused");
        inputArea.setColumns(50);
        inputArea.setRows(3);
        inputArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                e.consume();
                int value = (Character.toUpperCase(e.getKeyChar()) & 0xFF);
                inputArea.setText(String.format("%c => %c (%x) ", e.getKeyChar(), (char)value, value));
                memory.setByteRaw(address, value);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        JPanel bevel = new JPanel();
        bevel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        bevel.setLayout(new java.awt.BorderLayout());
        bevel.add(inputArea);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout());
        panel.add(bevel);

        JFrame frame = new JFrame("Keyboard");
//        frame.setResizable(false);
//        frame.setBounds(620, 370, 500, 300);
        frame.setContentPane(panel);

        return frame;
    }

    public void toggleView() {
        view.setVisible(!view.isVisible());
    }


    public JPanel createSettingsPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(4, 2, 0, 0));

        final JTextField txtAddr = GUI.createField(pane, "Address", Conversion.addrToHex(address), 10);

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int addr;
                try {
                    addr = SICXE.intToAddr(Conversion.hexToInt(txtAddr.getText()));
                } catch (NumberFormatException e) {
                    return;
                }
                setScreen(addr);
            }
        };

        txtAddr.addActionListener(al);

        return pane;
    }

}
