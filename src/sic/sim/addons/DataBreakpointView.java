package sic.sim.addons;

import sic.common.Conversion;
import sic.sim.Executor;
import sic.sim.breakpoints.DataBreakpoint;
import sic.sim.breakpoints.DataBreakpoints;
import sic.sim.vm.Memory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DataBreakpointView {

    Memory memory;
    DataBreakpoints dataBreakpoints;

    // GUI
    JFrame view;
    JTable table;

    // New / edit controls
    private JTextField fromField;
    private JTextField toField;
    private JCheckBox readBox;
    private JCheckBox writeBox;
    private JCheckBox enabledBox;
    private JButton addButton;
    private DefaultTableModel tableModel;

    public DataBreakpointView(Executor executor) {
        this.memory = executor.machine.memory;
        this.dataBreakpoints = this.memory.dataBreakpoints;
        this.view = createView();
    }

    private JFrame createView() {
        String column[] = {"From", "To", "Read", "Write", "Enabled"};

        table = new JTable() {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int i) {
                switch (i) {
                    case 0: return String.class;
                    case 1: return String.class;
                    case 2: return Boolean.class;
                    case 3: return Boolean.class;
                    case 4: return Boolean.class;
                    default: return String.class;
                }
            }

        };
//        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent listSelectionEvent) {
//                if (!listSelectionEvent.getValueIsAdjusting()) {
//
//                }
//            }
//        });
        tableModel = (DefaultTableModel)table.getModel();
        tableModel.setColumnIdentifiers(column);
        //tableModel.addRow(dataRow);
        tableModel.addTableModelListener(this::tableDataChange);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel bevelTable = new JPanel();
        bevelTable.setBorder(new BevelBorder(BevelBorder.LOWERED));
        bevelTable.setLayout(new BorderLayout());
        bevelTable.add(table);
        bevelTable.add(new JScrollPane(table));

        // ----
        // Control panel controls
        fromField = new JTextField("0x100", 6);
        toField = new JTextField("0x150", 6);
        readBox = new JCheckBox("Read");
        writeBox = new JCheckBox("Write", true);
        enabledBox = new JCheckBox("Enabled", true);
        addButton = new JButton("Add");
        addButton.addActionListener(this::addEdit);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(new Label("From"));
        controlPanel.add(fromField);
        controlPanel.add(new Label("To"));
        controlPanel.add(toField);
        controlPanel.add(readBox);
        controlPanel.add(writeBox);
        controlPanel.add(enabledBox);
        controlPanel.add(addButton);


        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout());
        panel.add(bevelTable, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        JFrame frame = new JFrame("Data breakpoints");
        frame.setResizable(false);
        frame.setBounds(620, 370, 600, 400);
        frame.setContentPane(panel);

        return frame;
    }

    private void addEdit(ActionEvent actionEvent) {
        int from = Conversion.hexToInt(fromField.getText().replaceFirst("0x", ""));
        int to = Conversion.hexToInt(toField.getText().replaceFirst("0x", ""));
        boolean read = readBox.isSelected();
        boolean write = writeBox.isSelected();
        boolean enabled = enabledBox.isSelected();

        DataBreakpoint selected = getSelectedBreakpoint();

//        if (selected == null) {
            selected = new DataBreakpoint(from, to, read, write);
            selected.setEnabled(enabled);
            addBreakpoint(selected);
//        } else {
//            selected.setRange(from, to);
//            selected.setAccess(DataBreakpoint.memoryAccessFromBool(read, write));
//            selected.setEnabled(enabled);
//            editBreakpoint(selected, table.getSelectedRow());
//        }
    }

    private void tableSelectedChanged(ActionEvent actionEvent) {

    }

    private DataBreakpoint getSelectedBreakpoint() {
        int selectedIndex = table.getSelectedRow();
        if (selectedIndex == -1) {
            return null;
        } else {
            return dataBreakpoints.at(selectedIndex);
        }
    }

//    private void updateSelectedControls(DataBreakpoint breakpoint) {
//        fromField.setText(Conversion.addrToHex(breakpoint.getFrom()));
//        toField.setText(Conversion.addrToHex(breakpoint.getTo()));
//
//    }

    private void tableDataChange(TableModelEvent tableModelEvent) {
        if (tableModelEvent.getType() == TableModelEvent.INSERT || tableModelEvent.getColumn() < 0) return;
        int row = tableModelEvent.getFirstRow();
        int column = tableModelEvent.getColumn();
        String columnName = tableModel.getColumnName(column);
        DataBreakpoint breakpoint = dataBreakpoints.at(row);
        Object data = tableModel.getValueAt(row, column);
        switch (columnName) {
            case "From":
                breakpoint.setRange(Conversion.hexToInt((String)data), breakpoint.getTo());
                break;
            case "To":
                breakpoint.setRange(breakpoint.getFrom(), Conversion.hexToInt((String)data));
                break;
            case "Read":
                breakpoint.setRead((Boolean) data);
                break;
            case "Write":
                breakpoint.setWrite((Boolean) data);
                break;
            case "Enabled":
                breakpoint.setEnabled((Boolean) data);
                break;
        }
    }

    private void addBreakpoint(DataBreakpoint breakpoint) {
        dataBreakpoints.add(breakpoint);
        tableModel.addRow(breakpoint.toTable());
        tableModel.fireTableDataChanged();
    }

    private void editBreakpoint(DataBreakpoint breakpoint, int index) {
        Object[] data = breakpoint.toTable();
        for (int i = 0; i < 5; i++) {
            tableModel.setValueAt(data[i], index, i);
        }
        tableModel.fireTableDataChanged();
    }


    public void updateView() {
        if (memory == null) return;
        StringBuilder sb = new StringBuilder();
    }

    public void toggleView() {
        view.setVisible(!view.isVisible());
    }

}
