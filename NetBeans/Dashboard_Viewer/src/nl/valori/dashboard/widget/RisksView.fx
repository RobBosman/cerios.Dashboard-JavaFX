/*
 * BudgetComponent.fx
 *
 * Created on 9-feb-2009, 17:26:31
 */

package nl.valori.dashboard.widget;

import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.effect.InnerShadow;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.KpiHolder;
import org.jfxtras.scene.layout.MigLayout;

import javafx.scene.Group;

import javafx.scene.text.TextOrigin;

/**
 * @author Rob
 */

public class RisksView extends CustomNode {

    public-init var guiComponent: GuiComponent;
    public var kpiHolder: KpiHolder;
    public var date: Date;

    def constants = new Constants();
    def xywh = constants.getXYWH(guiComponent.getLayout());
    def guiElementTotal = guiComponent.getGuiElement("RISK_TOTAL");

    def titleText = Text {
        font: constants.titleFont
        fill: constants.titleFontColor
        content: guiComponent.getTitle()
    }
    def insetRect = Rectangle {
        width: xywh[2]
        height: xywh[3]
        fill: bind constants.getAlertColor(getTotalAlertLevel(kpiHolder, date))
        effect: InnerShadow {
            offsetX: 2
            offsetY: 3
            color: Color.BLACK
        }
    }

    function createRiskMigNodes():Node[] {
        var migNodes: Node[] = [];
        var i = 1;
        var ready = false;
        while (not(ready)) {
            def guiElement = guiComponent.getGuiElement("RISK_{i}");
            if (guiElement == null) {
                ready = true;
            } else {
                var kpiVariable = guiElement.getKpiVariable();
                var valueSet = bind kpiHolder.getValueSet(kpiVariable);
                migNodes = [
                    migNodes,
                    Text {
                        font: constants.labelFont
                        fill: constants.backPlateFontColor
                        content: guiElement.getLabel()
                    },
                    Group {
                        content: [
                            Rectangle {
                                width: 50
                                height: 20
                                fill: bind constants.getAlertColor(kpiHolder.getAlertLevelAt(kpiVariable, date))
                            }
                            Text {
                                font: constants.valueFont
                                fill: constants.backPlateFontColor
                                content: bind formatPercentage(valueSet.getValueAt(date))
                                textOrigin: TextOrigin.TOP
                                translateX: 5
                                translateY: 5
                            }
                        ]
                        layoutInfo: MigLayout.nodeConstraints("ax right, wrap")
                    }
                ];
                i++;
            }
        }
        return migNodes;
    }

    function createTotalMigNodes():Node[] {
        if (guiElementTotal == null) {
            return [];
        }
        def sumLine = Line {
            startX: 0
            startY: 0
            endX: 100
            endY: 0
            strokeWidth: 1
            stroke: constants.backPlateFontColor
            layoutInfo: MigLayout.nodeConstraints("spanx, wrap")
        }
        def totaalLabelText = Text {
            font: constants.labelFont
            fill: constants.backPlateFontColor
            content: "Totaal"
        }
        def totaalValueText = Text {
            font: constants.titleFont
            content: bind formatPercentage(kpiHolder.getValueSet(guiElementTotal.getKpiVariable()).getValueAt(date))
            layoutInfo: MigLayout.nodeConstraints("ax right")
        }
        return [ sumLine, totaalLabelText, totaalValueText ];
    }

    function getTotalAlertLevel(kpiHolder:KpiHolder, date:Date):Integer {
        return kpiHolder.getAlertLevelAt(guiElementTotal.getKpiVariable(), date);
    }

    function formatPercentage(value:Double):String {
        return "{(value * 100.0).intValue()} %";
    }

    override public function create():Node {
        return MigLayout {
            content: [
                MigLayout.migNode(titleText, "wrap"),
                Group {
                    content: [
                        insetRect,
                        MigLayout {
                            rows: "[]0[]"
                            columns: "[]10[]"
                            content: [ createRiskMigNodes(), createTotalMigNodes() ]
                        }
                    ]
                    layoutInfo: MigLayout.nodeConstraints("grow, wrap")
                }
            ]
            translateX: xywh[0]
            translateY: xywh[1]
        }
    }
}