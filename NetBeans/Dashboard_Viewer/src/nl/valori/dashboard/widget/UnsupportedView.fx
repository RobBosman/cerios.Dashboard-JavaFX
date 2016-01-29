/*
 * BudgetComponent.fx
 *
 * Created on 9-feb-2009, 17:26:31
 */

package nl.valori.dashboard.widget;

import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.Period;

/**
 * @author Rob
 */

public class UnsupportedView extends CustomNode {

    public-init var guiComponent: GuiComponent;
    public var kpiHolder: KpiHolder;
    public var date: Date;

    override public function create():Node {
        return Group {
            content: [
                Rectangle {
                    width: guiComponent.getFloatProperty("width")
                    height: guiComponent.getFloatProperty("height")
                    fill: Color.RED
                    opacity: 0.7
                }
                Text {
                    content: "UNSUPPORTED TYPE: {guiComponent.getType()}"
                    fill: Color.WHITE
                    translateY: guiComponent.getFloatProperty("height") / 2
                }
            ]
            translateX: guiComponent.getFloatProperty("x")
            translateY: guiComponent.getFloatProperty("y")
        }
    }
}