package sic.sim.views;

import sic.ast.Symbol;
import sic.sim.Executor;
import sic.sim.vm.Memory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class WatchView {
    public JPanel mainPanel;
    private JTree tree;
    private DefaultTreeModel treeModel;

    private Executor executor;
    private Memory memory;

    private HashMap<Integer, Symbol> labelMap;
    private HashMap<Symbol, DefaultMutableTreeNode> nodeMap;

    public WatchView(Executor executor) {
        this.executor = executor;
        this.memory = executor.machine.memory;
        this.clearLabelMap();
    }


    public void setLabelMap(HashMap<Integer, Symbol> map) {
        this.labelMap = map;
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Watch");
        createNodes(top, map);

        treeModel = new DefaultTreeModel(top, true);
        tree.setModel(treeModel);
    }

    public void clearLabelMap() {
        this.labelMap = new HashMap<>();
        this.nodeMap = new HashMap<>();
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Watch (no symbols in object files)");

        treeModel = new DefaultTreeModel(top, true);
        tree.setModel(treeModel);
    }

    private void createNodes(DefaultMutableTreeNode root, HashMap<Integer, Symbol> map) {
        SortedSet<Integer> keys = new TreeSet<Integer>(map.keySet());
        for (Integer key : keys) {
            Symbol symbol = map.get(key);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(symbol.name + " - " + memory.getByteRaw(key + 2));
            root.add(node);
            nodeMap.put(symbol, node);
        }
    }

    private void refreshNodes() {
        for (Symbol symbol : labelMap.values()) {
            DefaultMutableTreeNode node = nodeMap.get(symbol);
            node.setUserObject(symbol.name + " - " + memory.getByteRaw(symbol.value() + 2));
            treeModel.nodeChanged(node);
        }
    }

    public void updateView() {
        refreshNodes();
    }
}
