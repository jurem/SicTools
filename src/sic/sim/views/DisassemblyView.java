package sic.sim.views;

import sic.ast.Command;
import sic.common.Conversion;
import sic.common.SICXE;
import sic.disasm.Disassembler;
import sic.sim.Breakpoints;
import sic.sim.Colors;
import sic.sim.Executor;
import sic.sim.vm.Machine;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;


class CellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setForeground(Colors.fg(isSelected, table.hasFocus()));
        c.setBackground(Colors.bg(isSelected, table.hasFocus()));
        setBorder(noFocusBorder);
        return c;
    }

}


@SuppressWarnings("serial")
class BreakpointIconCellRenderer extends CellRenderer {

    private ImageIcon icon;

    public BreakpointIconCellRenderer() {
        URL url = getClass().getResource("/img/rbp.png");
        if (url != null) icon = new ImageIcon(url);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setText("");
        if ("1".equals(value)) {
            if (icon != null) label.setIcon(icon);
            else label.setText(" o");
        } else {
            label.setIcon(null);
        }
        return label;
    }

}


/**
 * TODO: write a short description
 *
 * @author jure
 */
public class DisassemblyView {

    public static final int MOVE_SMALL = 1;

    private Executor executor;
    private Machine machine;
    private Breakpoints breakpoints;
    private Disassembler disassembler;
    private DefaultTableModel modelDis;

    public JPanel mainPanel;
    private JButton btnUp;
    private JButton btnDn;
    private JButton btnPC;
    private JTextField txtLoc;
    private JTable tabDis;

    public DisassemblyView(final Executor executor, final Disassembler disassembler) {
        this.executor = executor;
        this.machine = executor.machine;
        this.breakpoints = executor.breakpoints;
        this.disassembler = disassembler;

        $$$setupUI$$$();
        // disassembly table
        modelDis = (DefaultTableModel) tabDis.getModel();
        modelDis.setRowCount(16);
        modelDis.setColumnCount(6);
        tabDis.setBackground(Colors.bg);
        tabDis.setForeground(Colors.fg);
        tabDis.setSelectionBackground(Colors.selectionBg);
        tabDis.setSelectionForeground(Colors.selectionFg);
        tabDis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        CellRenderer cellRenderer = new CellRenderer();
        for (int i = 1; i < tabDis.getColumnCount(); i++)
            tabDis.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        tabDis.getColumnModel().getColumn(0).setCellRenderer(new BreakpointIconCellRenderer());
        tabDis.getColumnModel().getColumn(0).setPreferredWidth(20);
        tabDis.getColumnModel().getColumn(1).setPreferredWidth(60);
        tabDis.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabDis.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabDis.getColumnModel().getColumn(4).setPreferredWidth(120);
//        tabDis.getColumnModel().getColumn(2).setCellRenderer(new TooltipRenderer());

        btnUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                disMove(-MOVE_SMALL);
            }
        });
        btnDn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                disMove(MOVE_SMALL);
            }
        });
        btnPC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                disassembler.gotoPC();
                updateDis(true, false);
            }
        });
        txtLoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                disassembler.setLocation(SICXE.intToAddr(Conversion.hexToInt(txtLoc.getText())));
                updateDis(true, false);
            }
        });
        tabDis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    try {
                        int row = tabDis.rowAtPoint(evt.getPoint());
                        int col = tabDis.columnAtPoint(evt.getPoint());
                        if (col != 0) return;  // only first column
                        // get address from first column
                        int addr = SICXE.intToAddr(Conversion.hexToInt((String) tabDis.getValueAt(row, 1)));
                        breakpoints.toggleBreakpoint(addr);
                        updateBreakpoint(row, breakpoints.has(addr));
                    } catch (Exception e) {
                    }
                }
                super.mouseClicked(evt);
            }
        });
        tabDis.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        tabDis.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        tabDis.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space");
        tabDis.getActionMap().put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int row = tabDis.getSelectedRow();
                if (row < 0) return;
                if (--row < 0) {
                    row = 0;
                    disassembler.prev();
                    updateDis(false, false);
                }
                tabDis.setRowSelectionInterval(row, row);
            }
        });
        tabDis.getActionMap().put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int row = tabDis.getSelectedRow();
                if (row < 0) return;
                if (++row >= tabDis.getRowCount()) {
                    row = tabDis.getRowCount() - 1;
                    if ("".equals((tabDis.getValueAt(row, 1)))) return;
                    disassembler.next();
                    updateDis(false, false);
                }
                tabDis.setRowSelectionInterval(row, row);
            }
        });
        tabDis.getActionMap().put("space", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int row = tabDis.getSelectedRow();
                if (row < 0) return;
                String str = (String) tabDis.getValueAt(row, 1);
                if ("".equals(str)) return;
                int addr = SICXE.intToAddr(Conversion.hexToInt(str));
                breakpoints.toggleBreakpoint(addr);
                updateBreakpoint(row, breakpoints.has(addr));
            }
        });
    }

    private void createUIComponents() {
        tabDis = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void updateBreakpoint(int row, boolean active) {
        modelDis.setValueAt(active ? "1" : "", row, 0);
    }

    private void clearDisLine(int row) {
        modelDis.setValueAt("", row, 1);
        modelDis.setValueAt("", row, 2);
        modelDis.setValueAt("", row, 3);
        modelDis.setValueAt("", row, 4);
    }

    private void updateDisLine(int row, int addr, Command cmd) {
        updateBreakpoint(row, breakpoints.has(addr));
        modelDis.setValueAt(Conversion.addrToHex(addr), row, 1);
        modelDis.setValueAt(Conversion.bytesToHex(machine.memory.memory, addr, cmd.size()), row, 2);
        modelDis.setValueAt(cmd.nameToString(), row, 3);
        modelDis.setValueAt(cmd.operandToString(), row, 4);
//        modelDis.setValueAt(cmd.explain, row, 5);
    }

    public void updateDis(boolean selectPC, boolean followPC) {
        int loc = disassembler.location();
        for (int row = 0; row < tabDis.getRowCount(); row++) {
            if (loc > SICXE.MAX_ADDR) clearDisLine(row);
            else {
                if (selectPC && loc == machine.registers.getPC()) {
                    tabDis.setRowSelectionInterval(row, row);
                    followPC = false;
                }
                Command cmd = disassembler.disassembleSafe(loc);
                updateDisLine(row, loc, cmd);
                loc += cmd.size();
            }
        }
        if (followPC) {
            disassembler.gotoPC();
            updateDis(true, false);
        }
        txtLoc.setText(Conversion.addrToHex(disassembler.location()));
    }

    public void updateView(boolean selectPC, boolean followPC) {
        updateDis(selectPC, followPC);
    }

    void disMove(int count) {
        if (count > 0) disassembler.next(count);
        else disassembler.prev(-count);
        updateDis(false, false);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Disassembly"));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        mainPanel.add(panel1, BorderLayout.SOUTH);
        btnUp = new JButton();
        btnUp.setText("<");
        panel1.add(btnUp);
        txtLoc = new JTextField();
        txtLoc.setColumns(5);
        txtLoc.setText("00000");
        panel1.add(txtLoc);
        btnDn = new JButton();
        btnDn.setText(">");
        panel1.add(btnDn);
        btnPC = new JButton();
        btnPC.setText("PC");
        panel1.add(btnPC);
        mainPanel.add(tabDis, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
