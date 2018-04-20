package sic.sim.addons;

import sic.common.Conversion;
import sic.sim.Executor;
import sic.sim.breakpoints.DataBreakpoint;
import sic.sim.breakpoints.DataBreakpoints;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DataBreakpointView {

    private DataBreakpoints dataBreakpoints;

    // GUI
    private JFrame view;
    private JTable table;
    private DefaultTableModel tableModel;

    // New / edit controls
    private JTextField fromField, toField;
    private JCheckBox readBox, writeBox, enabledBox;
    private JButton addButton, editButton, removeButton;

    public DataBreakpointView(Executor executor) {
        this.dataBreakpoints = executor.machine.memory.dataBreakpoints;
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
        tableModel = (DefaultTableModel)table.getModel();
        tableModel.setColumnIdentifiers(column);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel bevelTable = new JPanel();
        bevelTable.setBorder(new BevelBorder(BevelBorder.LOWERED));
        bevelTable.setLayout(new BorderLayout());
        bevelTable.add(table);
        bevelTable.add(new JScrollPane(table));

        // ----
        // Control panel controls
        fromField = new JTextField("00100", 6);
        toField = new JTextField("00150", 6);
        readBox = new JCheckBox("Read");
        writeBox = new JCheckBox("Write", true);
        enabledBox = new JCheckBox("Enabled", true);
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        removeButton = new JButton("Remove");
        editButton.setEnabled(false); // Disabled until breakpoint is chosen
        removeButton.setEnabled(false); // Disabled until breakpoint is chosen

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
        controlPanel.add(editButton);
        controlPanel.add(removeButton);


        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout());
        panel.add(bevelTable, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        JFrame frame = new JFrame("Data breakpoints");
        frame.setResizable(false);
        frame.setBounds(620, 370, 700, 500);
        frame.setContentPane(panel);

        bindControls();

        return frame;
    }

    private void bindControls() {
        table.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
            // Handle update of selected element
            if (!listSelectionEvent.getValueIsAdjusting()) {
                if (!editButton.isEnabled()) {
                    editButton.setEnabled(true);
                    removeButton.setEnabled(true);
                }

                DataBreakpoint breakpoint = getSelectedBreakpoint();

                if (breakpoint == null) {
                    return;
                }

                fromField.setText(Conversion.addrToHex(breakpoint.getFrom()));
                toField.setText(Conversion.addrToHex(breakpoint.getTo()));
                readBox.setSelected(breakpoint.getRead());
                writeBox.setSelected(breakpoint.getWrite());
                enabledBox.setSelected(breakpoint.isEnabled());
            }
        });
        tableModel.addTableModelListener(tableModelEvent -> {
            if (tableModelEvent.getType() == TableModelEvent.INSERT || tableModelEvent.getColumn() < 0) return;
            int row = tableModelEvent.getFirstRow();
            int column1 = tableModelEvent.getColumn();
            String columnName = tableModel.getColumnName(column1);
            DataBreakpoint breakpoint = dataBreakpoints.at(row);
            Object data = tableModel.getValueAt(row, column1);
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
        });
        addButton.addActionListener(actionEvent -> {
            int from = Conversion.hexToInt(fromField.getText().replaceFirst("0x", ""));
            int to = Conversion.hexToInt(toField.getText().replaceFirst("0x", ""));
            boolean read = readBox.isSelected();
            boolean write = writeBox.isSelected();
            boolean enabled = enabledBox.isSelected();

            addBreakpoint(new DataBreakpoint(from, to, read, write, enabled));
        });
        editButton.addActionListener(actionEvent -> {
            int from = Conversion.hexToInt(fromField.getText().replaceFirst("0x", ""));
            int to = Conversion.hexToInt(toField.getText().replaceFirst("0x", ""));
            boolean read = readBox.isSelected();
            boolean write = writeBox.isSelected();
            boolean enabled = enabledBox.isSelected();

            DataBreakpoint selected = getSelectedBreakpoint();

            if (selected != null) {
                selected.setRange(from, to);
                selected.setRead(read);
                selected.setWrite(write);
                selected.setEnabled(enabled);
                editBreakpoint(selected, table.getSelectedRow());
            }
        });
        removeButton.addActionListener(actionEvent -> {
            int selectedIndex = table.getSelectedRow();
            dataBreakpoints.remove(selectedIndex);
            tableModel.removeRow(selectedIndex);
            tableModel.fireTableDataChanged();
        });
    }

    /**
     * Gets the currently selected breakpoint in table or null, if none selected.
     */
    private DataBreakpoint getSelectedBreakpoint() {
        int selectedIndex = table.getSelectedRow();
        if (selectedIndex == -1) {
            return null;
        } else {
            return dataBreakpoints.at(selectedIndex);
        }
    }

    /**
     * Adds the given breakpoint to data structure and to the table
     */
    private void addBreakpoint(DataBreakpoint breakpoint) {
        dataBreakpoints.add(breakpoint);
        tableModel.addRow(breakpoint.toTable());
        tableModel.fireTableDataChanged();
    }

    /**
     * Edits the given breakpoint in the table
     */
    private void editBreakpoint(DataBreakpoint breakpoint, int index) {
        Object[] data = breakpoint.toTable();
        for (int i = 0; i < 5; i++) {
            tableModel.setValueAt(data[i], index, i);
        }
        tableModel.fireTableDataChanged();
    }

    /**
     * Handles closing the view
     */
    public void toggleView() {
        view.setVisible(!view.isVisible());
    }

}
