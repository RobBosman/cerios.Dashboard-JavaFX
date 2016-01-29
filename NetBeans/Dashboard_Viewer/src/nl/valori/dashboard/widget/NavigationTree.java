/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.valori.dashboard.widget;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import nl.valori.dashboard.model.KpiHolder;

/**
 * @author Rob
 */
public class NavigationTree {

    class KpiHolderNode {

        private KpiHolder kpiHolder;

        public KpiHolderNode(KpiHolder kpiHolder) {
            this.kpiHolder = kpiHolder;
        }

        public KpiHolder getKpiHolder() {
            return kpiHolder;
        }

        @Override
        public String toString() {
            return kpiHolder.getName();
        }
    }
    private JTree tree;

    public JTree getTree() {
        return tree;
    }

    public KpiHolder getSelectedKpiHolder() {
        Object node = tree.getLastSelectedPathComponent();
        if (node == null) {
            TreePath treePath = tree.getPathForRow(0);
            tree.setSelectionPath(treePath);
            node = treePath.getLastPathComponent();
        }
        KpiHolderNode kpiHolderNode = (KpiHolderNode) ((DefaultMutableTreeNode) node).getUserObject();
        if (kpiHolderNode == null) {
            return null;
        }
        return kpiHolderNode.getKpiHolder();
    }

    public JComponent create(KpiHolder kpiHolderRoot) {
        // Create a tree that allows one selection at a time.
        tree = new JTree(createNode(kpiHolderRoot));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // TODO - Define Constants only once
        tree.setBackground(new Color(238, 238, 238));
        tree.setSelectionRow(0);
        // Create the scroll pane and add the tree to it.
        return new JScrollPane(tree);
    }

    public DefaultMutableTreeNode createNode(KpiHolder kpiHolder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new KpiHolderNode(kpiHolder));
        // Get a list with all child KpiHolders and sort it by name.
	java.util.List<KpiHolder> children = new ArrayList<KpiHolder>(kpiHolder.getChildren());
	Collections.sort(children, KpiHolder.COMPARATOR_NAME);
        // Recursively create a node for each child.
        for (KpiHolder child : children) {
            node.add(createNode(child));
        }
        return node;
    }
}