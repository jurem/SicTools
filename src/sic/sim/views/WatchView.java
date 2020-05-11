package sic.sim.views;

import sic.ast.StorageSymbol;
import sic.common.Conversion;
import sic.sim.Executor;
import sic.sim.breakpoints.DataBreakpoint;
import sic.sim.views.components.treetable.JTreeTable;
import sic.sim.views.components.treetable.TreeTableModel;
import sic.sim.vm.Memory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class WatchView {
    // gui
    public JPanel mainPanel;
    private JTreeTable treeTable;
    private WatchTreeTableModel treeTableModel;
    // data & communication with other views
    private Memory memory;
    private ActionListener updateBreakpointsEvent;
    // data & cached
    private HashMap<Integer, StorageSymbol> labelMap = new HashMap<>();
    private ArrayList<StorageSymbol> symbols;
    private int[] indicies;
    private HashMap<StorageSymbol, DataBreakpoint> breakpoints = new HashMap<>();

    public WatchView(Executor executor, ActionListener updateBreakpoints) {
        $$$setupUI$$$();
        this.memory = executor.machine.memory;
        this.updateBreakpointsEvent = updateBreakpoints;
        this.treeTableModel.setMemory(this.memory);
    }

    /**
     * Set the map of labels that the watch will display
     */
    public void setLabelMap(HashMap<Integer, StorageSymbol> map) {
        this.labelMap = map;

        // Sort by key and add to symbols
        Map<Integer, StorageSymbol> treeMap = new TreeMap<>(map);
        symbols = new ArrayList<>(map.size());
        for (Integer address : treeMap.keySet()) {
            symbols.add(map.get(address));
        }

        // Update the treeTable
        treeTableModel.updateSymbols(symbols);
        if (symbols.size() > 0) {
            indicies = generateIndices(symbols.size());
            treeTableModel.fireTreeNodesInserted(this, null, indicies, symbols.toArray());
            treeTable.getTree().expandPath(new TreePath(treeTableModel.getRoot()));
        }
    }

    /**
     * Reset the label map
     */
    public void clearLabelMap() {
        HashMap<Integer, StorageSymbol> oldMap = this.labelMap;
        this.labelMap = new HashMap<>();

        symbols = new ArrayList<>(oldMap.values());
        treeTableModel.updateSymbols(new ArrayList<>());
        if (symbols.size() > 0) {
            indicies = generateIndices(symbols.size());
            treeTableModel.fireTreeNodesRemoved(this, null, indicies, symbols.toArray());
            symbols = new ArrayList<>();
            for (DataBreakpoint breakpoint : breakpoints.values()) {
                memory.dataBreakpoints.remove(breakpoint);
            }
            breakpoints = new HashMap<>();
        }
    }

    /**
     * Update the values inside nodes
     */
    private void refreshNodes() {
        if (symbols != null && symbols.size() > 0) {
            treeTableModel.fireTreeNodesChanged(this, null, indicies, symbols.toArray());
        }
    }

    /**
     * Update the whole view
     */
    public void updateView() {
        refreshNodes();
    }

    /**
     * Called after initialization of the form
     */
    private void createUIComponents() {
        treeTableModel = new WatchTreeTableModel(memory);
        this.treeTable = new JTreeTable(treeTableModel);
        initTreeTable();
    }

    /**
     * Helper method for fire() functions on treeTable
     */
    private int[] generateIndices(int n) {
        int[] indices = new int[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        return indices;
    }

    /**
     * Initialize the table tree; set initial values...
     */
    private void initTreeTable() {
        // Set the treeTable column sizes
        this.treeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        this.treeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        this.treeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        this.treeTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        this.treeTable.getColumnModel().getColumn(4).setPreferredWidth(50);

        // Add a popup menu for toggling breakpoints
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem toggleReadBreakpoint = new JMenuItem("Toggle read breakpoint");
        JMenuItem toggleWriteBreakpoint = new JMenuItem("Toggle write breakpoint");
        JMenuItem toggleBothBreakpoint = new JMenuItem("Toggle read & write breakpoint");
        toggleReadBreakpoint.addActionListener(actionEvent -> toggleWatchBreakpoint(true, false));
        toggleWriteBreakpoint.addActionListener(actionEvent -> toggleWatchBreakpoint(false, true));
        toggleBothBreakpoint.addActionListener(actionEvent -> toggleWatchBreakpoint(true, true));
        popupMenu.add(toggleReadBreakpoint);
        popupMenu.add(toggleWriteBreakpoint);
        popupMenu.add(toggleBothBreakpoint);
        // Make the popup menu select the clicked item on right click
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                SwingUtilities.invokeLater(() -> {
                    int rowAtPoint = treeTable.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), treeTable));
                    if (rowAtPoint > -1) {
                        treeTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        treeTable.getTree().setSelectionRow(rowAtPoint);
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                // Do nothing special...
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
                // Do nothing special...
            }
        });
        this.treeTable.setComponentPopupMenu(popupMenu);
    }

    /**
     * Toggle the data breakpoint on currently selected symbol in the tree
     *
     * @param read  Should the read access be toggled
     * @param write Should write access be toggled
     */
    private void toggleWatchBreakpoint(boolean read, boolean write) {
        // Get selected symbol
        StorageSymbol selectedSymbol;
        Object selected = treeTable.getTree().getLastSelectedPathComponent();
        if (selected instanceof WatchRoot) return;
        else if (selected instanceof SymbolNode) selectedSymbol = ((SymbolNode) selected).getSymbol();
        else if (selected instanceof SymbolChild) selectedSymbol = ((SymbolChild) selected).symbol;
        else return;

        // Get the breakpoint
        DataBreakpoint existingBreakpoint = breakpoints.get(selectedSymbol);
        if (existingBreakpoint == null) {
            // Create a new one
            int start = selectedSymbol.value();
            int end = start + selectedSymbol.getElementCount() * selectedSymbol.getElementSize() - 1;
            DataBreakpoint newBreakpoint = new DataBreakpoint(start, end, read, write);
            breakpoints.put(selectedSymbol, newBreakpoint);
            memory.dataBreakpoints.add(newBreakpoint);
        } else {
            // Toggle existing one
            if (read) existingBreakpoint.toggleRead();
            if (write) existingBreakpoint.toggleWrite();
        }
        // Update the DataBreakpointsView
        updateBreakpointsEvent.actionPerformed(null);
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
        mainPanel.setMinimumSize(new Dimension(50, 200));
        mainPanel.setPreferredSize(new Dimension(100, 200));
        mainPanel.setRequestFocusEnabled(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Watch", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(98, 120));
        mainPanel.add(scrollPane1, BorderLayout.CENTER);
        Font treeTableFont = this.$$$getFont$$$("Lucida Console", -1, -1, treeTable.getFont());
        if (treeTableFont != null) treeTable.setFont(treeTableFont);
        scrollPane1.setViewportView(treeTable);
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
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}

class WatchTreeTableModel implements TreeTableModel {

    private String[] columnNames = {"Name", "Address", "Decimal", "Hex", "Char"};

    private Class[] columnTypes = {TreeTableModel.class, String.class, String.class, String.class, String.class};

    private WatchRoot root;

    private Memory memory;

    private EventListenerList listenerList = new EventListenerList();

    WatchTreeTableModel(Memory memory) {
        this.memory = memory;
        this.root = new WatchRoot(new ArrayList<>());
    }

    public void updateSymbols(List<StorageSymbol> symbols) {
        this.root.setSymbols(symbols);
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    /**
     * Returns the number of available columns.
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name for column number <code>column</code>.
     *
     * @param column
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Returns the type for column number <code>column</code>.
     *
     * @param column
     */
    @Override
    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }

    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     *
     * @param node
     * @param column
     */
    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof WatchRoot) {
            switch (column) {
                case 0: return "Watch";
                default: return "";
            }
        } else {
            StorageSymbol symbol;
            int address;
            if (node instanceof SymbolNode) {
                symbol = ((SymbolNode) node).getSymbol();
                address = symbol.value();
            } else {
                symbol = ((SymbolChild) node).symbol;
                address = symbol.value() + ((SymbolChild) node).index;
            }

            switch (column) {
                case 0:
                    return symbol.name; // To edit this you need to update the toString method of displayed element.
                case 1:
                    return Conversion.addrToHex(address);
                case 2:
                    switch (symbol.getDataType()) {
                        case BYTE:
                            return Integer.toString(this.memory.getByteRaw(address));
                        case WORD:
                            return Integer.toString(this.memory.getWordRaw(address));
                        case FLOAT:
                            return Double.toString(this.memory.getFloatRaw(address));
                    }
                case 3:
                    StringBuilder builder = new StringBuilder(symbol.getElementSize() * 2);
                    for (int i = 0; i < symbol.getElementSize(); i++) {
                        builder.append(String.format("%02X", this.memory.getByteRaw(address + i)));
                    }
                    return builder.toString();
                case 4:
                    switch (symbol.getDataType()) {
                        case BYTE:
                            return (char) this.memory.getByteRaw(address);
                        case WORD:
                            return (char) this.memory.getWordRaw(address);
                        default:
                            return "";
                    }
            }

        }
        return null;
    }

    /**
     * Indicates whether the the value for node <code>node</code>,
     * at column number <code>column</code> is editable.
     *
     * @param node
     * @param column
     */
    @Override
    public boolean isCellEditable(Object node, int column) {
        return columnTypes[column] == TreeTableModel.class;
    }

    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     *
     * @param aValue
     * @param node
     * @param column
     */
    @Override
    public void setValueAt(Object aValue, Object node, int column) { }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object o, int i) {
        if (o instanceof WatchRoot) return root.getSymbols().get(i);
        else if (o instanceof SymbolNode) return ((SymbolNode) o).getChildren().get(i);
        return null;
    }

    @Override
    public int getChildCount(Object o) {
        if (o instanceof WatchRoot) return root.getSymbols().size();
        else if (o instanceof SymbolNode) return ((SymbolNode) o).getChildrenCount();
        return 0;
    }

    @Override
    public boolean isLeaf(Object o) {
        if (o instanceof SymbolNode) return ((SymbolNode) o).getChildrenCount() == 0;
        else if (o instanceof WatchRoot) return ((WatchRoot) o).getSymbols().size() == 0;
        else if (o instanceof SymbolChild) return true;
        else return false;
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object o) { }

    @Override
    public int getIndexOfChild(Object o, Object o1) {
        if (o instanceof WatchRoot) {
            for (int i = 0; i < root.getSymbols().size(); i++) {
                if (root.getSymbols().get(i) == o1) return i;
            }
        } else if (o instanceof SymbolNode) {
            SymbolNode node = (SymbolNode) o;
            for (int i = 0; i < node.getChildrenCount(); i++) {
                if (node.getChildren().get(i) == o1) return i;
            }
        }
        return 0;
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
        listenerList.add(TreeModelListener.class, treeModelListener);

    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        listenerList.remove(TreeModelListener.class, treeModelListener);
    }


    /**
     * Notifies all listeners that have registered interest for notification on this event type.
     * The event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source
     * @param path
     * @param childIndicies
     * @param children
     */
    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndicies, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent event = null;

        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event
                if (event == null) event = new TreeModelEvent(source, path, childIndicies, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type.
     * The event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source
     * @param path
     * @param childIndicies
     * @param children
     */
    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndicies, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent event = null;

        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event
                if (event == null) event = new TreeModelEvent(source, path, childIndicies, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(event);
            }
        }
    }


    /**
     * Notifies all listeners that have registered interest for notification on this event type.
     * The event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source
     * @param path
     * @param childIndicies
     * @param children
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndicies, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent event = null;

        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event
                if (event == null) event = new TreeModelEvent(source, path, childIndicies, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(event);
            }
        }
    }



    /**
     * Notifies all listeners that have registered interest for notification on this event type.
     * The event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source
     * @param path
     * @param childIndicies
     * @param children
     */
    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndicies, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent event = null;

        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event
                if (event == null) event = new TreeModelEvent(source, path, childIndicies, children);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(event);
            }
        }
    }
}

/**
 * A class representing the root in the treeTable
 */
class WatchRoot {
    private List<SymbolNode> symbols = null;
    private List<StorageSymbol> storageSymbols = null;

    WatchRoot(List<StorageSymbol> symbols) {
        setSymbols(symbols);
    }

    public List<SymbolNode> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<StorageSymbol> symbols) {
        // Check if there is any actual update
        if (this.storageSymbols == symbols) return;
        this.storageSymbols = symbols;

        // Update the symbols
        if (symbols == null) {
            this.symbols = new ArrayList<>();
        } else {
            List<SymbolNode> nodes = new ArrayList<>(symbols.size());
            for (StorageSymbol symbol : symbols) {
                nodes.add(new SymbolNode(symbol));
            }
            this.symbols = nodes;
        }
    }

    @Override
    public String toString() {
        return "Watch";
    }
}

/**
 * A class representing a single storage symbol in the treeTable
 */
class SymbolNode {
    private final StorageSymbol symbol;
    private int childrenCount;
    private List<SymbolChild> children = null;

    SymbolNode(StorageSymbol symbol) {
        this.symbol = symbol;
        this.childrenCount = symbol.getElementCount() - 1;
    }

    public StorageSymbol getSymbol() {
        return symbol;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public List<SymbolChild> getChildren() {
        if (childrenCount == 0) return null;

        if (children == null) {
            ArrayList<SymbolChild> list = new ArrayList<>(childrenCount);
            for (int i = 1; i < symbol.getElementCount(); i++) {
                list.add(new SymbolChild(symbol, i * symbol.getElementSize()));
            }
            children = list;
        }

        return children;
    }

    @Override
    public String toString() {
        return symbol.toString();
    }
}

/**
 * A class representing the children of a storage symbol (in case of RESB..., array) in the treeTable
 */
class SymbolChild {
    public final StorageSymbol symbol;
    public final int index;
    SymbolChild(StorageSymbol symbol, int index) {
        this.symbol = symbol;
        this.index = index;
    }

    @Override
    public String toString() {
        return symbol.name + "[" + index / symbol.getElementSize() + "]";
    }
}