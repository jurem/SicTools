package sic.sim.views;

import sic.ast.Symbol;
import sic.sim.Executor;
import sic.sim.views.components.treetable.JTreeTable;
import sic.sim.views.components.treetable.TreeTableModel;
import sic.sim.vm.Memory;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.*;

public class WatchView {
    public JPanel mainPanel;
    private JTreeTable treeTable;
    private JTree tree;
    private DefaultTreeModel treeModel;

    private WatchTreeTableModel treeTableModel;

    private Executor executor;
    private Memory memory;

    private HashMap<Integer, Symbol> labelMap = new HashMap<>();

    public WatchView(Executor executor) {
        this.executor = executor;
        this.memory = executor.machine.memory;
        this.treeTableModel.setMemory(this.memory); // Sometimes createUIComponents gets called before constructor...
    }


    public void setLabelMap(HashMap<Integer, Symbol> map) {
        this.labelMap = map;

        ArrayList<Symbol> symbols = new ArrayList<>(map.values());
        treeTableModel.updateSymbols(symbols);
        if (symbols.size() > 0) {
            treeTableModel.fireTreeNodesInserted(this, null, generateIndices(symbols.size()), symbols.toArray());
        }
    }

    public void clearLabelMap() {
        HashMap<Integer, Symbol> oldMap = this.labelMap;
        this.labelMap = new HashMap<>();

        ArrayList<Symbol> symbols = new ArrayList<>(oldMap.values());
        treeTableModel.updateSymbols(new ArrayList<>());
        if (symbols.size() > 0) {
            treeTableModel.fireTreeNodesRemoved(this, null, generateIndices(symbols.size()), symbols.toArray());
        }
    }

    private void refreshNodes() {
        ArrayList<Symbol> symbols = new ArrayList<>(this.labelMap.values());
        treeTableModel.updateSymbols(symbols);
        treeTableModel.fireTreeNodesChanged(this, null, generateIndices(symbols.size()), symbols.toArray());
    }

    public void updateView() {
        refreshNodes();
    }

    private void createUIComponents() {
        treeTableModel = new WatchTreeTableModel(memory);
        this.treeTable = new JTreeTable(treeTableModel);
    }

    private int[] generateIndices(int n) {
        int[] indices = new int[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        return indices;
    }
}

class WatchTreeTableModel implements TreeTableModel {

    private String[] columnNames = {"Name", "Decimal", "Hex", "Char"};

    private Class[] columnTypes = {TreeTableModel.class, String.class, String.class, String.class};

    private WatchRoot root;

    private Memory memory;

    private EventListenerList listenerList = new EventListenerList();

    WatchTreeTableModel(Memory memory) {
        this.memory = memory;
        this.root = new WatchRoot(new ArrayList<>());
    }

    public void updateSymbols(List<Symbol> symbols) {
        this.root.symbols = symbols;
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
        } else if (node instanceof Symbol) {
            Symbol symbol = (Symbol) node;
            int memoryValue = this.memory.getByteRaw(symbol.value() + 2);
            switch (column) {
                case 0: return symbol.name;
                case 1: return Integer.toString(memoryValue);
                case 2: return Integer.toHexString(memoryValue);
                case 3: return (char) memoryValue;
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
        if (o == root) return root.symbols.get(i);
        return null;
    }

    @Override
    public int getChildCount(Object o) {
        if (o == root) return root.symbols.size();
        return 0;
    }

    @Override
    public boolean isLeaf(Object o) {
        if (o != root) return true;
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object o) { }

    @Override
    public int getIndexOfChild(Object o, Object o1) {
        if (o == root) {
            for (int i = 0; i < root.symbols.size(); i++) {
                if (root.symbols.get(i) == o1) return i;
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

class WatchRoot {
    public List<Symbol> symbols;

    WatchRoot(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return "Watch";
    }
}
