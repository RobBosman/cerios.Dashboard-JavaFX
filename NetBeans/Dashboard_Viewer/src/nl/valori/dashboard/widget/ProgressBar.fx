/*
 * ProgressBar.fx
 *
 * Created on 4-feb-2009, 16:29:09
 */

package nl.valori.dashboard.widget;

import java.lang.Math;
import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.KpiHolder;
import org.jfxtras.scene.layout.MigLayout;

/**
 * @author Rob
 */
public class ProgressBar extends CustomNode {

    public-init var toonGeplandTotaal: Boolean = true;
    public-init var guiComponent: GuiComponent;
    public var kpiHolder: KpiHolder;
    public var date: Date;

    def constants = new Constants();
    def xywh = constants.getXYWH(guiComponent.getLayout());
    def barHeight = 60;
    def spacing = 3;
    def graphWidth = xywh[2] - 2 * spacing;
    def tab = graphWidth * 2 / 3;

    def guiElementTarget = guiComponent.getGuiElement("TARGET");
    def guiElementActual = guiComponent.getGuiElement("ACTUAL");
    def kpiVariableTarget = guiElementTarget.getKpiVariable();
    def kpiVariableActual = guiElementActual.getKpiVariable();
    var valueSetTarget = bind kpiHolder.getValueSet(kpiVariableTarget);
    var valueSetActual = bind kpiHolder.getValueSet(kpiVariableActual);
    var thresholdMinValue = bind kpiVariableActual.getThreshold("MINIMUM").getThreshold(kpiHolder, date);
    var thresholdMaxValue = bind kpiVariableActual.getThreshold("MAXIMUM").getThreshold(kpiHolder, date);

    //var maximum = bind Math.max(valueSetTarget.getMaximum(kpiHolder.getFullPeriod()),
    //        valueSetActual.getMaximum(kpiHolder.getFullPeriod()));
    var maximum = bind valueSetTarget.getMaximum(kpiHolder.getFullPeriod());

    var targetValue = bind valueSetTarget.getValueAt(date);
    var actualValue = bind valueSetActual.getValueAt(date);

    var backColor = bind constants.getAlertColor(kpiHolder.getAlertLevelAt(kpiVariableActual, date));
    var midColor = constants.noSweatColor;

    def titleText = Text {
        font: constants.titleFont
        fill: constants.titleFontColor
        content: guiComponent.getTitle()
    }
    def minRect = Rectangle {
        width: bind getBarPos(thresholdMinValue)
        height: barHeight
        fill: bind backColor
    }
    def midRect = Rectangle {
        width: bind getBarPos(thresholdMaxValue)
        height: barHeight
        fill: midColor
    }
    def maxRect = Rectangle {
        width: graphWidth
        height: barHeight
        fill: bind backColor
    }
    def backPlate = Group {
        content: [ maxRect, midRect, minRect ]
        effect: InnerShadow{
            offsetX: 2
            offsetY: 2
            color: Color.BLACK
        }
    }
    def targetLine = Group {
        content: [
            Polygon {
                points: [
                    -4, -6,
                    4, -6,
                    0,  0
                ]
                fill: constants.targetColor
            },
            Polygon {
                points: [
                    -4, 6,
                    4, 6,
                    0, 0
                ]
                fill: constants.targetColor
                translateY: barHeight
            },
            Line {
                startX: 0
                startY: 0
                endX: 0
                endY: barHeight
                strokeWidth: 3
                stroke: constants.targetColor
            }
        ]
        translateX: bind getBarPos(targetValue)
    }
    def valueRect = Rectangle {
        y: 20
        width: bind getBarPos(actualValue)
        height: barHeight - 26
        fill: bind LinearGradient {
            startX: 0.0
            startY: 0.0
            endX: 0.0
            endY: 1.0
            proportional: true
            stops: [
                Stop {
                    offset: 0.0
                    color: constants.actualColor
                }
                Stop {
                    offset: 1.0
                    color: constants.actualGradientColor
                }
            ]
        }
        effect: DropShadow{
            offsetX: 2
            offsetY: 2
            color: Color.BLACK
        }
        opacity: bind if (valueSetActual.getFullPeriod().spans(date)) 1.0 else 0.5
    }

    def actualLabelText = Text {
        font: constants.labelFont
        fill: constants.fontColor
        content: guiElementActual.getLabel()
    }
    def actualValueText = Text {
        font: constants.valueFont
        fill: constants.fontColor
        content: bind "{guiElementActual.format(actualValue)} {guiElementActual.getUnit()}"
    }
    def targetLabelText = Text {
        font: constants.labelFont
        fill: constants.fontColor
        content: guiElementTarget.getLabel()
    }
    def targetValueText = Text {
        font: constants.valueFont
        fill: constants.fontColor
        content: bind "{guiElementActual.format(targetValue)} {guiElementTarget.getUnit()}"
    }
    def totalLabelText = Text {
        font: constants.labelFont
        fill: constants.fontColor
        content: "Total"
        visible: toonGeplandTotaal
    }
    def totalValueText = Text {
        font: constants.valueFont
        fill: constants.fontColor
        content: bind "{guiElementActual.format(maximum)} {guiElementActual.getUnit()}"
        visible: toonGeplandTotaal
    }

    bound function getBarPos(val:Float):Float {
        return Math.max(0, Math.min(graphWidth * val / maximum, graphWidth));
    }

    override public function create():Node {
        return MigLayout {
            content: [
                MigLayout.migNode(titleText, "cell 0 0 2 1"),
                Group {
                    content: [
                        backPlate, valueRect, targetLine
                    ]
                    layoutInfo: MigLayout.nodeConstraints("cell 0 1 2 1, grow")
                },
                MigLayout.migNode(actualLabelText, "cell 0 2 1 1"),
                MigLayout.migNode(actualValueText, "cell 1 2 1 1, ax right"),
                MigLayout.migNode(targetLabelText, "cell 0 3 1 1"),
                MigLayout.migNode(targetValueText, "cell 1 3 1 1, ax right"),
                MigLayout.migNode(totalLabelText, "cell 0 4 1 1"),
                MigLayout.migNode(totalValueText, "cell 1 4 1 1, ax right")
            ]
            translateX: xywh[0]
            translateY: xywh[1]
        }
    }
}