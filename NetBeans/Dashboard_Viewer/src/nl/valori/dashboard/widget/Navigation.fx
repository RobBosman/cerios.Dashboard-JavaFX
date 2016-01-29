/*
 * BudgetComponent.fx
 *
 * Created on 9-feb-2009, 17:26:31
 */

package nl.valori.dashboard.widget;

import javafx.ext.swing.SwingComponent;
import javafx.scene.CustomNode;
import javafx.scene.Node;
import javax.swing.event.TreeSelectionListener;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.widget.NavigationTree;

/**
 * @author Rob
 */
public class Navigation extends CustomNode {

    public-init var width: Float;
    public-init var height: Float;
    public-init var kpiHolderRoot: KpiHolder;

    def constants = new Constants();
    def navigationTree = new NavigationTree();
    def swingNavTree = navigationTree.create(kpiHolderRoot);
    def navTree = SwingComponent.wrap(swingNavTree);

    public var selectedKpiHolder = kpiHolderRoot;

    def navTreeSelectionListener = TreeSelectionListener {
        override function valueChanged(e){
            selectedKpiHolder = navigationTree.getSelectedKpiHolder();
            navigationTree.getTree().repaint();
        }
    }

    override public function create():Node {
        navTree.width = width;
        navTree.height = height;
        navigationTree.getTree().addTreeSelectionListener(navTreeSelectionListener);
        return navTree;
    }
}