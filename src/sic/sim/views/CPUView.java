package sic.sim.views;

import sic.ast.Command;
import sic.common.Conversion;
import sic.common.SICXE;
import sic.disasm.Disassembler;
import sic.sim.Executor;
import sic.sim.vm.Machine;
import sic.sim.vm.Registers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class CPUView {
    private final Color colorNochange = Color.BLACK;
    private final Color colorChange = Color.BLUE;

    private final Executor executor;
    private final Machine machine;
    private final Registers registers;
    private final Disassembler disassembler;

    private JTextField regA;
    private JTextField regX;
    private JTextField regL;
    private JTextField regS;
    private JTextField regT;
    private JTextField regB;
    private JTextField regSW;
    private JTextField regF;
    private JTextField regFF;
    private JTextField regPC;
    private JTextField txtInstruction;
    private JButton btnStep;
    private JButton btnStartStop;
    public JPanel mainPanel;
    private JLabel lblInfo;

    public CPUView(final Executor executor, final Disassembler disassembler) {
        this.executor = executor;
        this.machine = executor.getMachine();
        this.registers = machine.registers;
        this.disassembler = disassembler;

        btnStartStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (executor.isRunning()) {
                    executor.stop();
                    updateView();
                    //                cpuview.updateDis(true);
                    //                cpuview.tabDisassm.requestFocus();
                } else {
                    executor.start();
                }
            }
        });
        btnStep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                executor.step();
                updateView();
            }

        });
        regA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setA(Conversion.hexToInt(regA.getText()));
                updateView();
            }
        });
        regX.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setX(Conversion.hexToInt(regX.getText()));
                updateView();
            }
        });
        regL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setL(Conversion.hexToInt(regL.getText()));
                updateView();
            }
        });
        regS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setS(Conversion.hexToInt(regS.getText()));
                updateView();
            }
        });
        regT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setT(Conversion.hexToInt(regT.getText()));
                updateView();
            }
        });
        regB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setB(Conversion.hexToInt(regB.getText()));
                updateView();
            }
        });
        regSW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setSW(Conversion.hexToInt(regSW.getText()));
                updateView();
            }
        });
        regPC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setPC(Conversion.hexToInt(regPC.getText()));
                updateView();
            }
        });
        updateView();
    }

    private void updateRegWord(JTextField txt, int val) {
        String str = Conversion.wordToHex(val);
        if (txt.getText().equals(str))
            txt.setForeground(colorNochange);
        else {
            txt.setForeground(colorChange);
            txt.setText(str);
            txt.setToolTipText(String.format("<html>Sgn: %s<br>Dec: %s<br>Oct: %s<br>Bin: %s<html>",
                    SICXE.swordToInt(val), val, Integer.toOctalString(val), Integer.toBinaryString(val)));
        }
    }

    private void updateRegFloat(double val) {
        String str = Conversion.floatToHex(val);
        if (regF.getText().equals(str)) {
            regF.setForeground(colorNochange);
            regFF.setForeground(colorNochange);
        } else {
            regF.setForeground(colorChange);
            regFF.setForeground(colorChange);
            regF.setText(str);
            regFF.setText(Double.toString(val));
            //regF.setToolTipText("<html>sign, mantis, exp<html>"); TODO: sign, mantis, exp
        }
    }

    public void updateView() {
        if (machine == null) return;
        // registers
        updateRegWord(regA, registers.getA());
        updateRegWord(regX, registers.getX());
        updateRegWord(regL, registers.getL());
        updateRegWord(regS, registers.getS());
        updateRegWord(regT, registers.getT());
        updateRegWord(regB, registers.getB());
        updateRegWord(regSW, registers.getSW());
        updateRegFloat(registers.getF());
        updateRegWord(regPC, registers.getPC());
        //
        btnStartStop.setText(executor.isRunning() ? "Stop" : "Start");
        //
        Command cmd = disassembler.disassemble(registers.getPC());
        txtInstruction.setText(cmd == null ? "" : cmd.toString());
        lblInfo.setText(cmd == null ? "" : "<html>" + cmd.explain() + "</html>");
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setEnabled(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "CPU"));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setAlignmentX(0.5f);
        panel1.setAutoscrolls(false);
        panel1.setEnabled(true);
        mainPanel.add(panel1, BorderLayout.CENTER);
        final JLabel label1 = new JLabel();
        label1.setText("X");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(label1, gbc);
        regX = new JTextField();
        regX.setColumns(8);
        regX.setText("000002");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regX, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("L");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(label2, gbc);
        regL = new JTextField();
        regL.setColumns(8);
        regL.setText("000003");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regL, gbc);
        regS = new JTextField();
        regS.setText("000004");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regS, gbc);
        regA = new JTextField();
        regA.setColumns(8);
        regA.setText("000001");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regA, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("A");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("T");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(label5, gbc);
        regT = new JTextField();
        regT.setText("000005");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regT, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("B");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(label6, gbc);
        regB = new JTextField();
        regB.setText("000006");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regB, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("SW");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(label7, gbc);
        regSW = new JTextField();
        regSW.setText("000007");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regSW, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("F");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label8, gbc);
        regF = new JTextField();
        regF.setColumns(8);
        regF.setText("000000000008");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regF, gbc);
        regFF = new JTextField();
        regFF.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regFF, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("PC");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(label9, gbc);
        regPC = new JTextField();
        regPC.setText("000009");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(regPC, gbc);
        txtInstruction = new JTextField();
        txtInstruction.setText("LDA 0");
        txtInstruction.putClientProperty("html.disable", Boolean.FALSE);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(txtInstruction, gbc);
        btnStartStop = new JButton();
        btnStartStop.setText("Start");
        btnStartStop.setMnemonic('S');
        btnStartStop.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(btnStartStop, gbc);
        btnStep = new JButton();
        btnStep.setText("Step");
        btnStep.setMnemonic('T');
        btnStep.setDisplayedMnemonicIndex(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(btnStep, gbc);
        lblInfo = new JLabel();
        lblInfo.setFont(new Font("Courier", lblInfo.getFont().getStyle(), 12));
        lblInfo.setText("");
        lblInfo.setVerticalAlignment(1);
        lblInfo.setVerticalTextPosition(0);
        lblInfo.putClientProperty("html.disable", Boolean.FALSE);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(lblInfo, gbc);
        label1.setLabelFor(regX);
        label2.setLabelFor(regL);
        label3.setLabelFor(regS);
        label4.setLabelFor(regA);
        label5.setLabelFor(regT);
        label6.setLabelFor(regB);
        label7.setLabelFor(regSW);
        label8.setLabelFor(regF);
        label9.setLabelFor(regPC);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
