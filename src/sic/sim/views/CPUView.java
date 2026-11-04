package sic.sim.views;

import sic.ast.Command;
import sic.common.Conversion;
import sic.common.SICXE;
import sic.disasm.Disassembler;
import sic.sim.Executor;
import sic.sim.Colors;
import sic.sim.vm.Interrupt;
import sic.sim.vm.Machine;
import sic.sim.vm.Registers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class CPUView {
    private final Color colorNochange = Colors.fg;
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
    private JTextField timer;
    private JButton btnStep;
    private JButton btnStartStop;
    public JPanel mainPanel;
    private JLabel lblInfo;
    private JLabel lblInfoSW;

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
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setTimer(Integer.parseInt(timer.getText()));
                updateView();
            }
        });
        regF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setF(SICXE.bitsToFloat(Long.parseLong(regF.getText(), 16)));
                updateView();
            }
        });
        regFF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registers.setF(Double.parseDouble(regFF.getText()));
                updateView();
            }
        });
        updateView();
    }

    private void updateTimer(int val) {
        String str = String.format("%d", val);
        timer.setForeground(registers.intEnabled(Interrupt.IntClass.TIMER) ? Colors.fg : Colors.selectionInactiveFg);
        timer.setText(str);
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
        updateTimer(registers.getTimer());
        //
        btnStartStop.setText(executor.isRunning() ? "Stop" : "Start");
        //
        Command cmd = disassembler.disassemble(registers.getPC());
        txtInstruction.setText(cmd == null ? "" : cmd.toString());
        lblInfo.setText(cmd == null ? "" : "<html>" + cmd.explain() + "</html>");
        lblInfoSW.setText(cmd == null ? "" : "<html>" + registers.explainSW() + "</html>");
    }

    {
        setupUI();
    }

    private void setupUI() {
        GridBagConstraints gbc;

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setEnabled(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "CPU", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        // register A
        final JLabel labelA = new JLabel();
        labelA.setText("A");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelA, gbc);

        regA = new JTextField();
        regA.setColumns(8);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regA, gbc);

        // register x
        final JLabel labelX = new JLabel();
        labelX.setText("X");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelX, gbc);

        regX = new JTextField();
        regX.setColumns(8);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regX, gbc);

        // register L
        final JLabel labelL = new JLabel();
        labelL.setText("L");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelL, gbc);

        regL = new JTextField();
        regL.setColumns(8);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regL, gbc);

        // register S
        final JLabel labelS = new JLabel();
        labelS.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelS, gbc);

        regS = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regS, gbc);

        // register T
        final JLabel labelT = new JLabel();
        labelT.setText("T");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelT, gbc);

        regT = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regT, gbc);

        // register B
        final JLabel labelB = new JLabel();
        labelB.setText("B");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelB, gbc);

        regB = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regB, gbc);

        // timer
        final JLabel labelTimer = new JLabel();
        labelTimer.setText("TIM");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelTimer, gbc);

        timer = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(timer, gbc);

        // register F
        final JLabel labelF = new JLabel();
        labelF.setText("F");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(labelF, gbc);

        regF = new JTextField();
        regF.setColumns(8);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regF, gbc);

        regFF = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regFF, gbc);

        // register SW
        final JLabel labelSW = new JLabel();
        labelSW.setText("SW");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelSW, gbc);

        regSW = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regSW, gbc);

        // SW explanation
        lblInfoSW = new JLabel();
        Font lblInfoFont = this.$$$getFont$$$("Courier", -1, 12, lblInfoSW.getFont());
        if (lblInfoFont != null) lblInfoSW.setFont(lblInfoFont);
        lblInfoSW.putClientProperty("html.disable", Boolean.FALSE);
        lblInfoSW.setToolTipText("<html><b>MASK</b> shows which interrupts are <i>enabled</i>:<br>"
                +"<b>S</b>VC, <b>P</b>ROGRAM, <b>T</b>IMER or <b>I</b>O<br>"
                +"<b>CC:</b> <b>1</b> (LT), <b>2</b> (GT) or <b>0</b> (EQ)<br>"
                +"<b>IDLE:</b> <b>I</b>dle or <b>R</b>unning<br>"
                +"<b>MODE:</b> <b>U</b>ser or <b>S</b>upervisor");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 0, 0, 0);
        mainPanel.add(lblInfoSW, gbc);

        // register PC
        final JLabel labelPC = new JLabel();
        labelPC.setText("PC");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(labelPC, gbc);
        regPC = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(regPC, gbc);

        // disassembled instruction
        txtInstruction = new JTextField();
        txtInstruction.putClientProperty("html.disable", Boolean.FALSE);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(txtInstruction, gbc);

        // start/stop button
        btnStartStop = new JButton();
        btnStartStop.setText("Start");
        btnStartStop.setMnemonic('S');
        btnStartStop.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnStartStop, gbc);

        // step button
        btnStep = new JButton();
        btnStep.setText("Step");
        btnStep.setMnemonic('T');
        btnStep.setDisplayedMnemonicIndex(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnStep, gbc);

        // instruction explanation
        lblInfo = new JLabel();
        lblInfoFont = this.$$$getFont$$$("Courier", -1, 12, lblInfo.getFont());
        if (lblInfoFont != null) lblInfo.setFont(lblInfoFont);
        lblInfo.setPreferredSize(new Dimension(280, 120));
        lblInfo.setText("");
        lblInfo.setVerticalAlignment(1);
        lblInfo.setVerticalTextPosition(0);
        lblInfo.putClientProperty("html.disable", Boolean.FALSE);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 0, 0, 0);
        mainPanel.add(lblInfo, gbc);


        // spacer
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer1, gbc);

        // labels
        labelX.setLabelFor(regX);
        labelL.setLabelFor(regL);
        labelS.setLabelFor(regS);
        labelA.setLabelFor(regA);
        labelT.setLabelFor(regT);
        labelB.setLabelFor(regB);
        labelSW.setLabelFor(regSW);
        labelF.setLabelFor(regF);
        labelPC.setLabelFor(regPC);
        labelTimer.setLabelFor(timer);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
