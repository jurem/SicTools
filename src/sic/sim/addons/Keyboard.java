package sic.sim.addons;

import sic.common.Conversion;
import sic.common.GUI;
import sic.common.SICXE;
import sic.common.Logger;
import sic.sim.Executor;
import sic.sim.vm.Memory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Keyboard addon for SicTools
 *
 * Stores character value of latest key press into the memory
 * The Keyboard window has to be opened and focused
 */
public class Keyboard {

    public final int ADDRESS = 0xC000;

    private final Memory memory;
    // gui
    private final JFrame view;
    // settings
    private int address;
    private JTextArea inputArea;

    public Keyboard(final Executor executor) {
        this.memory = executor.getMachine().memory;
        this.view = createView();
        setScreen(ADDRESS);
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
                inputArea.setText(String.format("%c => %c (%d) ", e.getKeyChar(), (char)value, value));
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
